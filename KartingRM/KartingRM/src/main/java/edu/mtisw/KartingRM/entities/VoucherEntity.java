package edu.mtisw.KartingRM.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vouchers")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_code", unique = true, nullable = false)
    private String voucherCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm 'hrs.'")
    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "base_amount", nullable = false)
    private double baseAmount;

    @Column(name = "discount_amount", nullable = false)
    private double discountAmount;

    @Column(nullable = false)
    private double tax;

    @Column(nullable = false)
    private double total;

    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    @Transient
    private List<ParticipantPaymentDetail> paymentDetails = new ArrayList<>();

    /**
     * Genera automáticamente voucherCode e issueDate
     * si no han sido seteados antes de persistir.
     */
    @PrePersist
    private void prePersist() {
        if (voucherCode == null || voucherCode.isBlank()) {
            // Genera un código único acortado
            this.voucherCode = "VCH-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        }
        if (issueDate == null) {
            this.issueDate = LocalDateTime.now();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantPaymentDetail {
        private String name;
        private double baseAmount;
        private double groupDiscount;
        private double frequentDiscount;
        private double birthdayDiscount;
        private double netAmount;
        private double taxAmount;
        private double totalAmount;
    }
}
