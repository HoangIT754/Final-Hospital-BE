package com.hospital.backend.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "invoice")
public class Invoice extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id hóa đơn

    @OneToOne
    @JoinColumn(name = "medical_record_id")
    @NotNull
    MedicalRecord medicalRecord; // Hồ sơ bệnh án liên quan

    @Column(name = "total_amount")
    double totalAmount; // Tổng số tiền

    @Column(name = "status")
    String status; // Trạng thái thanh toán: UNPAID, PAID
}
