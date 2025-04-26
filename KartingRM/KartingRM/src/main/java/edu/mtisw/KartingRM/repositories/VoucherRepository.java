package edu.mtisw.KartingRM.repositories;

import edu.mtisw.KartingRM.entities.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {

    Optional<VoucherEntity> findByVoucherCode(String voucherCode);

    boolean existsByReservationId(Long reservationId);

    List<VoucherEntity> findAllByReservationReservationDateTimeBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
