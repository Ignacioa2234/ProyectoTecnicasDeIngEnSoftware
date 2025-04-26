package edu.mtisw.KartingRM.services;

import edu.mtisw.KartingRM.entities.KartEntity;
import edu.mtisw.KartingRM.entities.ParticipantEntity;
import edu.mtisw.KartingRM.entities.ReservationEntity;
import edu.mtisw.KartingRM.entities.ClientEntity;
import edu.mtisw.KartingRM.repositories.ReservationRepository;
import edu.mtisw.KartingRM.repositories.ClientRepository;
import edu.mtisw.KartingRM.repositories.KartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private KartRepository kartRepository;

    public ReservationEntity createReservation(ReservationEntity reservation) {
        // 1) Validar cliente
        if (reservation.getClient() == null || reservation.getClient().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar el id del cliente que hace la reserva"
            );
        }
        Long clientId = reservation.getClient().getId();
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el cliente con id " + clientId
                ));
        reservation.setClient(client);

        // 1.5) Evitar doble reserva del mismo cliente a la misma hora
        LocalDateTime when = reservation.getReservationDateTime();
        List<ReservationEntity> ya = reservationRepository
                .findAllByClientAndReservationDateTime(client, when);
        if (!ya.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cliente ya tiene otra reserva para " + when
            );
        }

        // 2) Auto-asignar karts si lista vacía
        int needed = reservation.getPeopleCount();
        if (reservation.getAssignedKarts() == null || reservation.getAssignedKarts().isEmpty()) {
            List<KartEntity> libres = new ArrayList<>();
            for (KartEntity kart : kartRepository.findAll()) {
                List<ReservationEntity> conflicts =
                        reservationRepository.findConflictByKartAndDateTime(kart.getId(), when);
                if (conflicts.isEmpty()) {
                    libres.add(kart);
                    if (libres.size() == needed) break;
                }
            }
            if (libres.size() < needed) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No hay suficientes karts libres para esa fecha/hora"
                );
            }
            reservation.setAssignedKarts(libres);
        }

        // 3) Validar participantes
        if (reservation.getParticipants() == null
                || reservation.getParticipants().size() != needed) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de participantes (" +
                            (reservation.getParticipants() == null ? 0 : reservation.getParticipants().size()) +
                            ") debe coincidir con el número de personas (" + needed + ")"
            );
        }
        for (ParticipantEntity p : reservation.getParticipants()) {
            if (p.getName() == null || p.getEmail() == null || p.getBirthDate() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cada participante necesita name, email y birthDate"
                );
            }
            p.setReservation(reservation);
        }

        // 4) El email del cliente debe estar entre los participantes
        boolean included = reservation.getParticipants().stream()
                .anyMatch(p -> p.getEmail().equals(client.getEmail()));
        if (!included) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El email del cliente (" + client.getEmail() +
                            ") debe estar en la lista de participantes"
            );
        }

        // 5) Comprobar conflictos finales
        for (KartEntity kart : reservation.getAssignedKarts()) {
            List<ReservationEntity> conflicts =
                    reservationRepository.findConflictByKartAndDateTime(
                            kart.getId(),
                            when
                    );
            if (!conflicts.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "El kart " + kart.getId() +
                                " ya está reservado a las " + when
                );
            }
        }

        // 6) Guardar y devolver
        return reservationRepository.save(reservation);
    }

    public Optional<ReservationEntity> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Optional<ReservationEntity> getReservationByCode(String code) {
        return reservationRepository.findByReservationCode(code);
    }

    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<ReservationEntity> getReservationsByClient(ClientEntity client) {
        return reservationRepository.findAllByClient(client);
    }

    public List<ReservationEntity> getReservationsBetween(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findAllByReservationDateTimeBetween(start, end);
    }

    public ReservationEntity updateReservation(ReservationEntity reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
