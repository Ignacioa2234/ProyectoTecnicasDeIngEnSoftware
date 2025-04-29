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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        // id nulo
        VoucherEntity v = new VoucherEntity();
        v.setReservation(new ReservationEntity());
        assertThrows(
          org.springframework.web.server.ResponseStatusException.class,
          () -> voucherService.createVoucher(v)
        );

        // conflicto: ya existe
        ReservationEntity r = new ReservationEntity();
        r.setId(2L);
        v.setReservation(r);
        when(voucherRepo.existsByReservationId(2L)).thenReturn(true);
        assertThrows(
          org.springframework.web.server.ResponseStatusException.class,
          () -> voucherService.createVoucher(v)
        );
    }

    @Test
    void createVoucher_success() {
        // preparamos reserva con cliente y participante válido
        ReservationEntity r = new ReservationEntity();
        r.setId(3L);
        r.setPeopleCount(1);
        r.setMaxLapsOrTime(10);
        r.setReservationDateTime(LocalDateTime.now());
        ClientEntity cli = new ClientEntity();
        cli.setEmail("a@b");
        r.setClient(cli);

        ParticipantEntity p = new ParticipantEntity();
        p.setName("X");
        p.setEmail(cli.getEmail());
        p.setBirthDate(LocalDate.now());
        p.setReservation(r);
        r.setParticipants(List.of(p));

        when(reservationRepo.findById(3L)).thenReturn(Optional.of(r));
        when(voucherRepo.existsByReservationId(3L)).thenReturn(false);
        when(voucherRepo.save(any(VoucherEntity.class))).thenAnswer(i -> i.getArgument(0));

        VoucherEntity req = new VoucherEntity();
        req.setReservation(r);

        VoucherEntity out = voucherService.createVoucher(req);
        assertNotNull(out.getVoucherCode());
        assertEquals(r, out.getReservation());
        assertFalse(out.getPaymentDetails().isEmpty());
        verify(voucherRepo).save(any());
    }

    @Test
    void sendVoucherEmail_success() throws Exception {
        // preparamos voucher mínimo para enviar correo
        ReservationEntity r = new ReservationEntity();
        r.setReservationCode("C1");
        r.setReservationDateTime(LocalDateTime.now());
        r.setMaxLapsOrTime(10);
        r.setPeopleCount(1);
        ClientEntity client = new ClientEntity();
        client.setName("Usuario");
        r.setClient(client);

        VoucherEntity v = new VoucherEntity();
        v.setVoucherCode("V1");
        v.setReservation(r);
        // agregamos un detalle para poblar la tabla
        VoucherEntity.ParticipantPaymentDetail d = new VoucherEntity.ParticipantPaymentDetail(
            "Usuario", 100.0, 10.0, 5.0, 2.0, 83.0, 15.77, 98.77
        );
        v.setPaymentDetails(List.of(d));

        MimeMessage msg = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(msg);

        voucherService.sendVoucherEmail(v, "dest@x.com");

        verify(mailSender).send(msg);
    }
}
