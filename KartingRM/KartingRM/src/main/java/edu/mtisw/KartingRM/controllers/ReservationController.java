package edu.mtisw.KartingRM.controllers;

import edu.mtisw.KartingRM.entities.ReservationEntity;
import edu.mtisw.KartingRM.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin("*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationEntity> createReservation(
            @RequestBody ReservationEntity reservation
    ) {
        // 1) Validamos que los emails de grupo coincidan con el número de personas
        if (reservation.getGroupEmails() == null
                || reservation.getGroupEmails().size() != reservation.getPeopleCount()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The number of group emails does not match the people count"
            );
        }

        // 2) Delegamos en el service toda la lógica de persistencia y asignación de karts
        ReservationEntity created = reservationService.createReservation(reservation);

        // 3) Construimos la URI de la nueva reserva: /api/reservations/{id}
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        // 4) Devolvemos 201 Created, con cabecera Location y body con el objeto completo
        return ResponseEntity
                .created(location)
                .body(created);
    }

    @GetMapping(params = {"start","end"})
    public ResponseEntity<List<ReservationEntity>> getReservationsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<ReservationEntity> list = reservationService.getReservationsBetween(start, end);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ReservationEntity>> getAllReservations() {
        List<ReservationEntity> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationById(@PathVariable Long id) {
        Optional<ReservationEntity> reservationOpt = reservationService.getReservationById(id);
        return reservationOpt
                .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ReservationEntity> getReservationByCode(@PathVariable String code) {
        Optional<ReservationEntity> reservationOpt = reservationService.getReservationByCode(code);
        return reservationOpt
                .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationEntity> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationEntity reservationDetails
    ) {
        Optional<ReservationEntity> opt = reservationService.getReservationById(id);
        if (opt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ReservationEntity existing = opt.get();
        existing.setReservationCode(reservationDetails.getReservationCode());
        existing.setReservationDateTime(reservationDetails.getReservationDateTime());
        existing.setMaxLapsOrTime(reservationDetails.getMaxLapsOrTime());
        existing.setPeopleCount(reservationDetails.getPeopleCount());
        ReservationEntity updated = reservationService.updateReservation(existing);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
