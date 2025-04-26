package edu.mtisw.KartingRM.services;

import edu.mtisw.KartingRM.entities.ClientEntity;
import edu.mtisw.KartingRM.repositories.ClientRepository;
import edu.mtisw.KartingRM.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public ClientEntity createClient(ClientEntity client) {
        return clientRepository.save(client);
    }

    public Optional<ClientEntity> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public List<ClientEntity> getAllClients() {
        return clientRepository.findAll();
    }

    public ClientEntity updateClient(ClientEntity client) {
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public double calculateDiscountForClient(Long clientId) {
        // Retrieve the client from the repository
        Optional<ClientEntity> clientOpt = clientRepository.findById(clientId);
        if (!clientOpt.isPresent()) {
            return 0.0;
        }
        ClientEntity client = clientOpt.get();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

        long reservationCount = reservationRepository.countByClientAndReservationDateTimeBetween(client, startOfMonth, endOfMonth);

        if (reservationCount >= 7) {
            return 0.30;
        } else if (reservationCount >= 5) {
            return 0.20;
        } else if (reservationCount >= 2) {
            return 0.10;
        } else {
            return 0.0;
        }
    }

    public Optional<ClientEntity> login(String email, String password) {
        return clientRepository.findByEmailAndPassword(email, password);
    }
}
