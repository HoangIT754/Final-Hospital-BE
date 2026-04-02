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
@Table(name = "lab_test_order")
public class LabTestOrder extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id yêu cầu xét nghiệm

    @Column(name = "order_code", unique = true, length = 20)
    String orderCode;

    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    @NotNull
    MedicalRecord medicalRecord; // Hồ sơ bệnh án liên quan

    @Column(name = "test_type")
    String testType; // Loại xét nghiệm (máu, nước tiểu, X-quang...)

    @Column(name = "status")
    String status; // Trạng thái: PENDING, PROCESSING, COMPLETED
}
