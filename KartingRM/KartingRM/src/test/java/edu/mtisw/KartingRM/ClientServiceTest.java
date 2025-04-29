// src/test/java/edu/mtisw/KartingRM/ClientServiceTest.java
package edu.mtisw.KartingRM;

import edu.mtisw.KartingRM.entities.ClientEntity;
import edu.mtisw.KartingRM.repositories.ClientRepository;
import edu.mtisw.KartingRM.repositories.ReservationRepository;
import edu.mtisw.KartingRM.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock ClientRepository clientRepo;
    @Mock ReservationRepository reservationRepo;
    @InjectMocks ClientService clientService;

    private ClientEntity client;
    private LocalDateTime startOfMonth, endOfMonth;

    @BeforeEach
    void setup() {
        client = new ClientEntity();
        client.setId(1L);
        var today = LocalDate.now();
        startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        endOfMonth   = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(23,59,59);
    }

    @Test
    void createClient_delegatesToRepo() {
        when(clientRepo.save(client)).thenReturn(client);
        assertSame(client, clientService.createClient(client));
        verify(clientRepo).save(client);
    }

    @Test
    void getClientById_found() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        assertTrue(clientService.getClientById(1L).isPresent());
    }

    @Test
    void getClientById_notFound() {
        when(clientRepo.findById(2L)).thenReturn(Optional.empty());
        assertTrue(clientService.getClientById(2L).isEmpty());
    }

    @Test
    void getAllClients_delegatesToRepo() {
        when(clientRepo.findAll()).thenReturn(List.of(client));
        assertEquals(1, clientService.getAllClients().size());
    }

    @Test
    void updateClient_delegatesToRepo() {
        when(clientRepo.save(client)).thenReturn(client);
        assertSame(client, clientService.updateClient(client));
    }

    @Test
    void deleteClient_delegatesToRepo() {
        doNothing().when(clientRepo).deleteById(1L);
        clientService.deleteClient(1L);
        verify(clientRepo).deleteById(1L);
    }

    @Test
    void calculateDiscount_noClient() {
        when(clientRepo.findById(42L)).thenReturn(Optional.empty());
        assertEquals(0.0, clientService.calculateDiscountForClient(42L));
    }

    @Test
    void calculateDiscount_tiers() {
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));
        // <2
        when(reservationRepo.countByClientAndReservationDateTimeBetween(client, startOfMonth, endOfMonth))
            .thenReturn(1L);
        assertEquals(0.0, clientService.calculateDiscountForClient(1L));
        // 2–4
        when(reservationRepo.countByClientAndReservationDateTimeBetween(client, startOfMonth, endOfMonth))
            .thenReturn(3L);
        assertEquals(0.10, clientService.calculateDiscountForClient(1L));
        // 5–6
        when(reservationRepo.countByClientAndReservationDateTimeBetween(client, startOfMonth, endOfMonth))
            .thenReturn(5L);
        assertEquals(0.20, clientService.calculateDiscountForClient(1L));
        // ≥7
        when(reservationRepo.countByClientAndReservationDateTimeBetween(client, startOfMonth, endOfMonth))
            .thenReturn(7L);
        assertEquals(0.30, clientService.calculateDiscountForClient(1L));
    }

    @Test
    void login_delegatesToRepo() {
        when(clientRepo.findByEmailAndPassword("x","y")).thenReturn(Optional.of(client));
        assertTrue(clientService.login("x","y").isPresent());
    }
}
