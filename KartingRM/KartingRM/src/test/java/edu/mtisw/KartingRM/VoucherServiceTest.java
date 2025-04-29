// src/test/java/edu/mtisw/KartingRM/VoucherServiceTest.java
package edu.mtisw.KartingRM;

import edu.mtisw.KartingRM.entities.ClientEntity;
import edu.mtisw.KartingRM.entities.ParticipantEntity;
import edu.mtisw.KartingRM.entities.ReservationEntity;
import edu.mtisw.KartingRM.entities.VoucherEntity;
import edu.mtisw.KartingRM.repositories.ReservationRepository;
import edu.mtisw.KartingRM.repositories.VoucherRepository;
import edu.mtisw.KartingRM.services.VoucherService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepo;
    @Mock
    private ReservationRepository reservationRepo;
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private VoucherService voucherService;

    @Test
    void getByIdAndCode_delegates() {
        VoucherEntity v = new VoucherEntity();
        when(voucherRepo.findById(5L)).thenReturn(Optional.of(v));
        assertTrue(voucherService.getVoucherById(5L).isPresent());
        when(voucherRepo.findByVoucherCode("X")).thenReturn(Optional.of(v));
        assertTrue(voucherService.getVoucherByCode("X").isPresent());
    }

    @Test
    void getAllAndDelete_delegates() {
        VoucherEntity v = new VoucherEntity();
        when(voucherRepo.findAll()).thenReturn(List.of(v));
        assertEquals(1, voucherService.getAllVouchers().size());
        doNothing().when(voucherRepo).deleteById(7L);
        voucherService.deleteVoucher(7L);
        verify(voucherRepo).deleteById(7L);
    }

    @Test
    void createVoucher_errorBranches() {
        // 1) id de reserva no indicado
        VoucherEntity v1 = new VoucherEntity();
        v1.setReservation(new ReservationEntity());
        assertThrows(
            ResponseStatusException.class,
            () -> voucherService.createVoucher(v1)
        );

        // 2) conflicto: ya existe voucher para esa reserva
        ReservationEntity r2 = new ReservationEntity();
        r2.setId(2L);
        VoucherEntity v2 = new VoucherEntity();
        v2.setReservation(r2);
        when(voucherRepo.existsByReservationId(2L)).thenReturn(true);
        assertThrows(
            ResponseStatusException.class,
            () -> voucherService.createVoucher(v2)
        );
    }

    @Test
    void createVoucher_success() {
        // --- Preparar cliente ---
        ClientEntity client = new ClientEntity();
        client.setEmail("a@b.com");

        // --- Preparar participante ---
        ParticipantEntity p = new ParticipantEntity();
        p.setName("Juan");
        p.setEmail(client.getEmail());
        p.setBirthDate(LocalDate.now());

        // --- Preparar reserva ---
        ReservationEntity r = new ReservationEntity();
        r.setId(3L);
        r.setClient(client);
        r.setPeopleCount(1);
        r.setMaxLapsOrTime(15);
        r.setReservationDateTime(LocalDateTime.now());
        r.setParticipants(List.of(p));
        p.setReservation(r);

        // --- Mocks de repos ---
        when(reservationRepo.findById(3L)).thenReturn(Optional.of(r));
        when(voucherRepo.existsByReservationId(3L)).thenReturn(false);
        when(voucherRepo.save(any(VoucherEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // --- Invocar servicio ---
        VoucherEntity req = new VoucherEntity();
        req.setReservation(r);
        VoucherEntity out = voucherService.createVoucher(req);

        // --- Verificaciones ---
        assertEquals(r, out.getReservation(), "La reserva debe propagarse al voucher");
        assertNotNull(out.getPaymentDetails(), "Debe generarse la lista de detalles");
        assertFalse(out.getPaymentDetails().isEmpty(), "La lista de detalles no debe quedar vacía");
        assertTrue(out.getTotal() > 0, "El total calculado debe ser mayor que 0");
        verify(voucherRepo).save(any());
    }

    @Test
    void sendVoucherEmail_success() throws Exception {
        // --- Preparar reserva mínimas para el correo ---
        ReservationEntity r = new ReservationEntity();
        r.setReservationCode("C1");
        r.setReservationDateTime(LocalDateTime.now());
        r.setMaxLapsOrTime(10);
        r.setPeopleCount(1);
        ClientEntity cli = new ClientEntity();
        cli.setName("Usuario");
        r.setClient(cli);

        VoucherEntity v = new VoucherEntity();
        v.setVoucherCode("V1");
        v.setReservation(r);
        // un detalle para poblar la tabla del PDF
        VoucherEntity.ParticipantPaymentDetail d =
            new VoucherEntity.ParticipantPaymentDetail(
                "Usuario", 100.0, 10.0, 5.0, 2.0, 83.0, 15.77, 98.77
            );
        v.setPaymentDetails(List.of(d));

        MimeMessage msg = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(msg);

        voucherService.sendVoucherEmail(v, "dest@x.com");

        verify(mailSender).send(msg);
    }
}
