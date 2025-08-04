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
@Table(name = "prescription")
public class Prescription extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id đơn thuốc

    @OneToOne
    @JoinColumn(name = "medical_record_id")
    @NotNull
    MedicalRecord medicalRecord; // Hồ sơ bệnh án liên quan

    @Column(name = "notes")
    String notes; // Ghi chú từ bác sĩ về việc dùng thuốc
}
