package edu.mtisw.KartingRM.repositories;

import edu.mtisw.KartingRM.entities.ReservationEntity;
import edu.mtisw.KartingRM.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    Optional<ReservationEntity> findByReservationCode(String code);

    List<ReservationEntity> findAllByClient(ClientEntity client);

    List<ReservationEntity> findAllByReservationDateTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByClientAndReservationDateTimeBetween(ClientEntity client, LocalDateTime start, LocalDateTime end);

    List<ReservationEntity> findAllByClientAndReservationDateTime(
            ClientEntity client,
            LocalDateTime reservationDateTime
    );

    @Query("SELECT COUNT(r) FROM ReservationEntity r " +
            "WHERE r.client.id = :clientId " +
            "  AND FUNCTION('MONTH', r.reservationDateTime) = :month")
    long countByClientAndMonth(@Param("clientId") Long clientId,
                               @Param("month") int month);

    @Query("""
        SELECT r
        FROM ReservationEntity r
        JOIN r.assignedKarts k
        WHERE k.id = :kartId
          AND r.reservationDateTime = :dateTime
    """)
    List<ReservationEntity> findConflictByKartAndDateTime(
            @Param("kartId") Long kartId,
            @Param("dateTime") LocalDateTime dateTime
    );
}
