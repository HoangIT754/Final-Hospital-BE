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
@Table(name = "medical_record")
public class MedicalRecord extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id của hồ sơ khám bệnh

    @OneToOne
    @JoinColumn(name = "appointment_id")
    @NotNull
    Appointment appointment; // Lịch khám liên quan

    @Column(name = "symptoms")
    String symptoms; // Triệu chứng bệnh nhân mô tả

    @Column(name = "diagnosis")
    String diagnosis; // Kết luận chẩn đoán của bác sĩ

    @Column(name = "notes")
    String notes; // Ghi chú thêm từ bác sĩ
}
