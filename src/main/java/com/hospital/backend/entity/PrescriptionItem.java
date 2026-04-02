package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "prescription_item")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionItem extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    @NotNull
    Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    @NotNull
    Medicine medicine;

    @Column(name = "dosage")         // liều: 500mg, 10ml...
    String dosage;

    @Column(name = "frequency")      // 2 lần/ngày, 1 lần buổi tối...
    String frequency;

    @Column(name = "duration_days")  // số ngày: 5 ngày, 7 ngày...
    Integer durationDays;

    @Column(name = "quantity")       // tổng số viên/lọ cấp cho bệnh nhân
    Integer quantity;

    @Column(name = "route")          // đường dùng: ORAL, IV, IM, TOPICAL...
    String route;

    @Column(name = "instruction")    // hướng dẫn riêng cho từng thuốc
    String instruction;
}

