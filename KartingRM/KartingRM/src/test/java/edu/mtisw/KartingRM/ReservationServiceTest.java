// src/test/java/edu/mtisw/KartingRM/ReservationServiceTest.java
package edu.mtisw.KartingRM;

import edu.mtisw.KartingRM.entities.ClientEntity;
import edu.mtisw.KartingRM.entities.KartEntity;
import edu.mtisw.KartingRM.entities.ParticipantEntity;
import edu.mtisw.KartingRM.entities.ReservationEntity;
import edu.mtisw.KartingRM.repositories.ClientRepository;
import edu.mtisw.KartingRM.repositories.KartRepository;
import edu.mtisw.KartingRM.repositories.ReservationRepository;
import edu.mtisw.KartingRM.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepo;
    @Mock
    private ClientRepository      clientRepo;
    @Mock
    private KartRepository        kartRepo;
    @InjectMocks
    private ReservationService    service;

    private ClientEntity       client;
    private ReservationEntity  baseRes;
    private LocalDateTime      now;

    @BeforeEach
    void setup() {
        now    = LocalDateTime.now();
        client = new ClientEntity();
        client.setId(1L);
        client.setEmail("cliente@demo.com"); 

        baseRes = new ReservationEntity();
        baseRes.setClient(client);
        baseRes.setReservationDateTime(now);
        baseRes.setPeopleCount(1);
        baseRes.setAssignedKarts(Collections.emptyList());
    }

    @Test
    void createReservation_noClient_throws() {
        ReservationEntity r = new ReservationEntity();
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(r)
        );
    }

    @Test
    void createReservation_clientIdNull_throws() {
        ReservationEntity r = new ReservationEntity();
        r.setClient(new ClientEntity());
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(r)
        );
    }

    @Test
    void createReservation_clientNotFound_throws() {
        when(clientRepo.findById(1L)).thenReturn(Optional.empty());
        baseRes.getClient().setId(1L);
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(baseRes)
        );
    }

    @Test
    void createReservation_doubleBooking_throws() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepo.findAllByClientAndReservationDateTime(client, now))
            .thenReturn(List.of(new ReservationEntity()));
        baseRes.getClient().setId(1L);
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(baseRes)
        );
    }

    @Test
    void createReservation_notEnoughKarts_throws() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepo.findAllByClientAndReservationDateTime(client, now))
            .thenReturn(Collections.emptyList());
        KartEntity k = new KartEntity(); k.setId(99L);
        when(kartRepo.findAll()).thenReturn(List.of(k));
        when(reservationRepo.findConflictByKartAndDateTime(99L, now))
            .thenReturn(Collections.emptyList());
        baseRes.getClient().setId(1L);
        baseRes.setPeopleCount(2);
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(baseRes)
        );
    }

    @Test
    void createReservation_participantsNull_throws() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepo.findAllByClientAndReservationDateTime(client, now))
            .thenReturn(Collections.emptyList());
        KartEntity k = new KartEntity(); k.setId(1L);
        when(kartRepo.findAll()).thenReturn(List.of(k));
        when(reservationRepo.findConflictByKartAndDateTime(1L, now))
            .thenReturn(Collections.emptyList());
        baseRes.getClient().setId(1L);
        baseRes.setPeopleCount(1);
        // participants == null
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(baseRes)
        );
    }

    @Test
    void createReservation_participantMissingFields_throws() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepo.findAllByClientAndReservationDateTime(client, now))
            .thenReturn(Collections.emptyList());
        KartEntity k = new KartEntity(); k.setId(1L);
        when(kartRepo.findAll()).thenReturn(List.of(k));
        when(reservationRepo.findConflictByKartAndDateTime(1L, now))
            .thenReturn(Collections.emptyList());
        baseRes.getClient().setId(1L);
        baseRes.setPeopleCount(1);
        ParticipantEntity p = new ParticipantEntity();
        // falta name/email/birthDate
        baseRes.setParticipants(List.of(p));
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(baseRes)
        );
    }

    @Test
    void createReservation_clientEmailNotInParticipants_throws() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepo.findAllByClientAndReservationDateTime(client, now))
            .thenReturn(Collections.emptyList());
        KartEntity k = new KartEntity(); k.setId(1L);
        when(kartRepo.findAll()).thenReturn(List.of(k));
        when(reservationRepo.findConflictByKartAndDateTime(1L, now))
            .thenReturn(Collections.emptyList());
        baseRes.getClient().setId(1L);
        baseRes.setPeopleCount(1);
        ParticipantEntity p = new ParticipantEntity();
        p.setName("X"); p.setEmail("otro@x.com"); p.setBirthDate(now.toLocalDate());
        baseRes.setParticipants(List.of(p));
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(baseRes)
        );
    }

    @Test
    void createReservation_kartConflict_throws() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepo.findAllByClientAndReservationDateTime(client, now))
            .thenReturn(Collections.emptyList());
        KartEntity k = new KartEntity(); k.setId(1L);
        when(kartRepo.findAll()).thenReturn(List.of(k));
        // primer check OK, segundo encuentra conflicto
        when(reservationRepo.findConflictByKartAndDateTime(1L, now))
            .thenReturn(Collections.emptyList(), List.of(new ReservationEntity()));
        baseRes.getClient().setId(1L);
        baseRes.setPeopleCount(1);
        ParticipantEntity p = new ParticipantEntity();
        p.setName("X"); p.setEmail(client.getEmail()); p.setBirthDate(now.toLocalDate());
        baseRes.setParticipants(List.of(p));
        assertThrows(ResponseStatusException.class,
            () -> service.createReservation(baseRes)
        );
    }

    @Test
    void createReservation_success() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        when(reservationRepo.findAllByClientAndReservationDateTime(client, now))
            .thenReturn(Collections.emptyList());

        KartEntity k = new KartEntity(); k.setId(1L);
        when(kartRepo.findAll()).thenReturn(List.of(k));
        when(reservationRepo.findConflictByKartAndDateTime(1L, now))
            .thenReturn(Collections.emptyList());

        ParticipantEntity p = new ParticipantEntity();
        p.setName("Juan");
        p.setEmail(client.getEmail());                  
        p.setBirthDate(LocalDate.now());                
        baseRes.setParticipants(List.of(p));

        when(reservationRepo.save(baseRes)).thenReturn(baseRes);

        var out = service.createReservation(baseRes);
        assertSame(baseRes, out);
        verify(reservationRepo).save(baseRes);
    }
    @Test
    void delegations_work() {
        var r = new ReservationEntity();
        when(reservationRepo.findById(9L)).thenReturn(Optional.of(r));
        assertTrue(service.getReservationById(9L).isPresent());

        when(reservationRepo.findByReservationCode("C")).thenReturn(Optional.of(r));
        assertTrue(service.getReservationByCode("C").isPresent());

        when(reservationRepo.findAll()).thenReturn(List.of(r));
        assertEquals(1, service.getAllReservations().size());

        when(reservationRepo.findAllByClient(client)).thenReturn(List.of(r));
        assertEquals(1, service.getReservationsByClient(client).size());

        when(reservationRepo
          .findAllByReservationDateTimeBetween(now, now.plusHours(1)))
          .thenReturn(List.of(r));
        assertEquals(1, service.getReservationsBetween(now, now.plusHours(1)).size());

        when(reservationRepo.save(r)).thenReturn(r);
        assertSame(r, service.updateReservation(r));

        doNothing().when(reservationRepo).deleteById(5L);
        service.deleteReservation(5L);
        verify(reservationRepo).deleteById(5L);
    }
}
