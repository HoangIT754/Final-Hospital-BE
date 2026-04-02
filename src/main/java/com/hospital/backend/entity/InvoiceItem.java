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
@Table(name = "invoice_item")
public class InvoiceItem extends AuditModel{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    Invoice invoice;

    // Liên kết ngược về nguồn (option, cho tương lai)
    @Column(name = "source_type")
    String sourceType; // "PRESCRIPTION_ITEM", "LAB_TEST_ORDER_DETAIL", ...

    @Column(name = "source_id")
    UUID sourceId;

    @Column(name = "description", nullable = false)
    String description; // Tên thuốc / tên dịch vụ hiển thị

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "unit_price", precision = 12, scale = 2)
    BigDecimal unitPrice;  // snapshot giá tại thời điểm lập hoá đơn

    @Column(name = "currency", nullable = false)
    String currency = "VND";

    @Column(name = "line_total", precision = 12, scale = 2)
    BigDecimal lineTotal; // unitPrice * quantity
}
