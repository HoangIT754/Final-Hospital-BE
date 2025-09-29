package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
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
    @JoinColumn(name = "user_id")
    User user; // Tham chiếu đến user (tài khoản đăng nhập) - có thể null nếu bệnh nhân chưa có account

    @Column(name = "first_name", nullable = false)
    String firstName; // tên của bệnh nhân

    @Column(name = "last_name", nullable = false)
    String lastName; // họ của bệnh nhân

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth; // ngày tháng năm sinh

    @Column(name = "gender")
    String gender; // giới tính

    @Column(name = "address")
    String address; // địa chỉ của bệnh nhân

    @Column(name = "phone_number", length = 20)
    String phoneNumber; // số điện thoại của bệnh nhân

    @Column(name = "identity_number", unique = true)
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
    @JoinColumn(name = "status_id", nullable = false)
    PatientStatus status; // Trạng thái hiện tại của bệnh nhân

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;
}
