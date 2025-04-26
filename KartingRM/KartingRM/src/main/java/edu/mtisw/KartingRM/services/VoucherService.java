package edu.mtisw.KartingRM.services;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
import edu.mtisw.KartingRM.entities.ParticipantEntity;
import edu.mtisw.KartingRM.entities.ReservationEntity;
import edu.mtisw.KartingRM.entities.VoucherEntity;
import edu.mtisw.KartingRM.repositories.ReservationRepository;
import edu.mtisw.KartingRM.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JavaMailSender mailSender;

    public VoucherEntity createVoucher(VoucherEntity voucher) {
        if (voucher.getReservation() == null || voucher.getReservation().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar id de la reserva");
        }
        Long resId = voucher.getReservation().getId();

        if (voucherRepository.existsByReservationId(resId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un voucher para la reserva " + resId
            );
        }

        ReservationEntity res = reservationRepository.findById(resId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe reserva con id " + resId));
        voucher.setReservation(res);

        int count = res.getPeopleCount();
        double tarifa = switch (res.getMaxLapsOrTime()) {
            case 15 -> 20000;
            case 20 -> 25000;
            default -> 15000;
        };
        double tarifaUnit = tarifa;

        double groupRate = count >= 3 && count <= 5 ? 0.10
                : count >= 6 && count <= 10 ? 0.20
                : count >= 11 ? 0.30 : 0.0;

        int month = res.getReservationDateTime().getMonthValue();
        long visits = reservationRepository.countByClientAndMonth(
                res.getClient().getId(), month);
        double freqRate = visits >= 7 ? 0.30
                : visits >= 5 ? 0.20
                : visits >= 2 ? 0.10 : 0.0;

        int maxBirth = 1;
        if (count >= 6 && count <= 10) {
            maxBirth = 2;
        }
        int usedBirth = 0;
        LocalDateTime resDate = res.getReservationDateTime();

        List<VoucherEntity.ParticipantPaymentDetail> details = new ArrayList<>();
        for (ParticipantEntity p : res.getParticipants()) {
            double dGrupo = tarifaUnit * groupRate;
            double dFreq = p.getEmail().equals(res.getClient().getEmail())
                    ? tarifaUnit * freqRate : 0.0;
            double dBirth = 0.0;
            LocalDate bd = p.getBirthDate();
            if (usedBirth < maxBirth
                    && bd.getDayOfMonth() == resDate.getDayOfMonth()
                    && bd.getMonthValue() == resDate.getMonthValue()) {
                dBirth = tarifaUnit * 0.50;
                usedBirth++;
            }
            double mayorDesc = Math.max(dGrupo, Math.max(dFreq, dBirth));
            double neto = tarifaUnit - mayorDesc;
            double tax = neto * 0.19;
            double total = neto + tax;

            details.add(new VoucherEntity.ParticipantPaymentDetail(
                    p.getName(), tarifaUnit,
                    dGrupo, dFreq, dBirth,
                    neto, tax, total
            ));
        }
        voucher.setPaymentDetails(details);

        double baseAmount = tarifaUnit * count;
        double discountAmount = details.stream()
                .mapToDouble(d -> tarifaUnit - d.getNetAmount())
                .sum();
        double taxAmount = details.stream()
                .mapToDouble(VoucherEntity.ParticipantPaymentDetail::getTaxAmount)
                .sum();
        double totalAmount = details.stream()
                .mapToDouble(VoucherEntity.ParticipantPaymentDetail::getTotalAmount)
                .sum();

        voucher.setBaseAmount(baseAmount);
        voucher.setDiscountAmount(discountAmount);
        voucher.setTax(taxAmount);
        voucher.setTotal(totalAmount);
        if (voucher.getIssueDate() == null) {
            voucher.setIssueDate(LocalDateTime.now());
        }

        return voucherRepository.save(voucher);
    }

    public Optional<VoucherEntity> getVoucherById(Long id) {
        return voucherRepository.findById(id);
    }

    public Optional<VoucherEntity> getVoucherByCode(String code) {
        return voucherRepository.findByVoucherCode(code);
    }

    public List<VoucherEntity> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public VoucherEntity updateVoucher(VoucherEntity voucher) {
        return createVoucher(voucher);
    }

    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }

    public void sendVoucherEmail(VoucherEntity voucher, String recipientEmail) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Información de la Reserva:"));
            document.add(new Paragraph("• Código: " + voucher.getReservation().getReservationCode()));
            document.add(new Paragraph("• Fecha y hora: " + voucher.getReservation().getReservationDateTime()));
            document.add(new Paragraph("• Vueltas/Tiempo: " + voucher.getReservation().getMaxLapsOrTime()));
            document.add(new Paragraph("• Personas: " + voucher.getReservation().getPeopleCount()));
            document.add(new Paragraph("• Cliente: " + voucher.getReservation().getClient().getName()));
            document.add(new Paragraph(" ")); // línea en blanco

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            for (String header : new String[]{
                    "Nombre",
                    "Tarifa Base",
                    "Desc. Grupo",
                    "Desc. Frec.",
                    "Desc. Cumple",
                    "Neto",
                    "IVA",
                    "Total"
            }) {
                PdfPCell cell = new PdfPCell(new Paragraph(header));
                table.addCell(cell);
            }

            for (VoucherEntity.ParticipantPaymentDetail d : voucher.getPaymentDetails()) {
                table.addCell(d.getName());
                table.addCell(String.format("%.0f", d.getBaseAmount()));
                table.addCell(String.format("%.0f", d.getGroupDiscount()));
                table.addCell(String.format("%.0f", d.getFrequentDiscount()));
                table.addCell(String.format("%.0f", d.getBirthdayDiscount()));
                table.addCell(String.format("%.0f", d.getNetAmount()));
                table.addCell(String.format("%.0f", d.getTaxAmount()));
                table.addCell(String.format("%.0f", d.getTotalAmount()));
            }
            document.add(table);

            document.close();
            byte[] pdfBytes = baos.toByteArray();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("ignacio23012002@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject("Voucher: " + voucher.getVoucherCode());
            helper.setText("Adjunto encontrarás tu voucher con todos los detalles de la reserva y los montos individualizados.");
            helper.addAttachment("voucher.pdf", new ByteArrayResource(pdfBytes));
            mailSender.send(message);

        } catch (DocumentException | MessagingException ex) {
            throw new RuntimeException("Error generando o enviando el PDF del voucher", ex);
        }
    }
}
