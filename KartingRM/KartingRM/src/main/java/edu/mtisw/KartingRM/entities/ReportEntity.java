package edu.mtisw.KartingRM.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportType;

    private String monthName;

    private LocalDateTime reportDate;

    private String aggregationKey;

    private BigDecimal totalIncome;

    private Long reservationCount;

    public ReportEntity(String reportType,
                        String monthName,
                        String aggregationKey,
                        BigDecimal totalIncome,
                        Long reservationCount) {
        this.reportType       = reportType;
        this.monthName        = monthName;
        this.reportDate       = LocalDateTime.now();
        this.aggregationKey   = aggregationKey;
        this.totalIncome      = totalIncome.setScale(0, RoundingMode.HALF_UP);
        this.reservationCount = reservationCount;
    }

    public ReportEntity(String reportType,
                        String monthName,
                        String aggregationKey,
                        Double totalIncome,
                        Long reservationCount) {
        this(reportType,
                monthName,
                aggregationKey,
                totalIncome != null
                        ? BigDecimal.valueOf(totalIncome).setScale(0, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO,
                reservationCount);
    }
}
