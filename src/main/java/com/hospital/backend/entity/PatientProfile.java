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
@Table(name = "patient_profile")
public class PatientProfile extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id của hồ sơ bệnh nhân

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    User user; // Tham chiếu đến user (tài khoản đăng nhập)

    @Column(name = "identity_number")
    String identityNumber; // Số CCCD / CMND của bệnh nhân

    @Column(name = "health_insurance_number")
    String healthInsuranceNumber; // Số thẻ bảo hiểm y tế

    @Column(name = "emergency_contact")
    String emergencyContact; // Số điện thoại người liên hệ khẩn cấp

    @Column(name = "medical_history")
    String medicalHistory; // Tiền sử bệnh lý của bệnh nhân

    @Column(name = "allergies")
    String allergies; // Dị ứng (thuốc, thức ăn...)

    @ManyToOne
    @JoinColumn(name = "status_id")
    @NotNull
    PatientStatus status; // Trạng thái hiện tại của bệnh nhân
}
