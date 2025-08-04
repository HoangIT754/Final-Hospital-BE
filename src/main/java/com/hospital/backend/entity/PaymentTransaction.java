package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id giao dịch thanh toán

    @OneToOne
    @JoinColumn(name = "invoice_id")
    @NotNull
    Invoice invoice; // Tham chiếu hóa đơn liên quan

    @Column(name = "payment_method")
    String paymentMethod; // Phương thức thanh toán: CASH, QR, BANK_TRANSFER

    @Column(name = "paid_at")
    LocalDateTime paidAt; // Thời điểm thanh toán
}
