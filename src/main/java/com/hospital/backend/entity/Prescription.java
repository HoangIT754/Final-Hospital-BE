package com.hospital.backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "prescription")
public class Prescription extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id đơn thuốc

    @OneToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    @NotNull
    MedicalRecord medicalRecord; // Hồ sơ bệnh án liên quan

    @Column(name = "notes")
    String notes; // Ghi chú chung của bác sĩ cho cả đơn thuốc

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PrescriptionStatus status = PrescriptionStatus.NEW;
    // NEW, DISPENSED, CANCELLED, ...

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PrescriptionItem> items;

    public enum PrescriptionStatus {
        NEW,
        PARTIALLY_DISPENSED,
        DISPENSED,
        CANCELLED
    }
}
