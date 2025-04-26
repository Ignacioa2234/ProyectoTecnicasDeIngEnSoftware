package edu.mtisw.KartingRM.repositories;

import edu.mtisw.KartingRM.entities.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    @Query("""
        SELECT new edu.mtisw.KartingRM.entities.ReportEntity(
            'laps',
            CAST(FUNCTION('MONTHNAME', v.reservation.reservationDateTime) AS string),
            CAST(v.reservation.maxLapsOrTime AS string),
            SUM(v.total),
            COUNT(v)
        )
        FROM VoucherEntity v
        WHERE v.issueDate BETWEEN :start AND :end
        GROUP BY
            FUNCTION('YEAR', v.reservation.reservationDateTime),
            FUNCTION('MONTH', v.reservation.reservationDateTime),
            CAST(FUNCTION('MONTHNAME', v.reservation.reservationDateTime) AS string),
            v.reservation.maxLapsOrTime
        ORDER BY
            FUNCTION('YEAR', v.reservation.reservationDateTime),
            FUNCTION('MONTH', v.reservation.reservationDateTime)
    """ )
    List<ReportEntity> reportByLapsOrTime(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );

    @Query("""
        SELECT new edu.mtisw.KartingRM.entities.ReportEntity(
            'group-size',
            CAST(FUNCTION('MONTHNAME', v.reservation.reservationDateTime) AS string),
            CASE
              WHEN v.reservation.peopleCount BETWEEN 1 AND 2 THEN '1-2 personas'
              WHEN v.reservation.peopleCount BETWEEN 3 AND 5 THEN '3-5 personas'
              WHEN v.reservation.peopleCount BETWEEN 6 AND 10 THEN '6-10 personas'
              ELSE '11+ personas'
            END,
            SUM(v.total),
            COUNT(v)
        )
        FROM VoucherEntity v
        WHERE v.issueDate BETWEEN :start AND :end
        GROUP BY
            FUNCTION('YEAR', v.reservation.reservationDateTime),
            FUNCTION('MONTH', v.reservation.reservationDateTime),
            CAST(FUNCTION('MONTHNAME', v.reservation.reservationDateTime) AS string),
            CASE
              WHEN v.reservation.peopleCount BETWEEN 1 AND 2 THEN '1-2 personas'
              WHEN v.reservation.peopleCount BETWEEN 3 AND 5 THEN '3-5 personas'
              WHEN v.reservation.peopleCount BETWEEN 6 AND 10 THEN '6-10 personas'
              ELSE '11+ personas'
            END
        ORDER BY
            FUNCTION('YEAR', v.reservation.reservationDateTime),
            FUNCTION('MONTH', v.reservation.reservationDateTime)
    """ )
    List<ReportEntity> reportByGroupSize(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );

    List<ReportEntity> findAllByReportType(String reportType);
}
