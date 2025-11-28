package com.hospital.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payment")
public class Payment extends AuditModel{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    Invoice invoice;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    BigDecimal amount;

    @Column(name = "currency", nullable = false)
    String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    PaymentMethod method; // CASH, CARD, MOMO, BANK_TRANSFER...

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PaymentStatus status = PaymentStatus.PENDING;
    // PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED

    @Column(name = "paid_at")
    java.time.LocalDateTime paidAt;

    public enum PaymentMethod {
        CASH,
        CARD,
        MOMO,
        BANK_TRANSFER
    }

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED,
        REFUNDED
    }
}
