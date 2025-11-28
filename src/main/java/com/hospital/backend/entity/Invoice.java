package com.hospital.backend.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "invoice")
public class Invoice extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "code", unique = true)
    String code; // INV-2025-0001...

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    PatientProfile patient;

    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    MedicalRecord medicalRecord;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    InvoiceType type = InvoiceType.PHARMACY;
    // PHARMACY, LAB, CONSULTATION, MIXED...

    @Column(name = "subtotal", precision = 12, scale = 2)
    BigDecimal subtotal;      // tổng trước giảm giá/thuế

    @Column(name = "discount_amount", precision = 12, scale = 2)
    BigDecimal discountAmount;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 12, scale = 2)
    BigDecimal totalAmount;   // số tiền phải trả

    @Column(name = "currency", nullable = false)
    String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    InvoiceStatus status = InvoiceStatus.UNPAID;
    // DRAFT, UNPAID, PARTIALLY_PAID, PAID, CANCELLED, REFUNDED

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    List<InvoiceItem> items;

    @OneToMany(mappedBy = "invoice")
    List<Payment> payments;

    public enum InvoiceStatus {
        DRAFT,
        UNPAID,
        PARTIALLY_PAID,
        PAID,
        CANCELLED,
        REFUNDED
    }

    public enum InvoiceType {
        PHARMACY,
        LAB,
        CONSULTATION,
        MIXED
    }
}
