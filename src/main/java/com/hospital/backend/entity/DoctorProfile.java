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
@Table(name = "doctor_profile")
public class DoctorProfile extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id của hồ sơ bác sĩ

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    User user; // Tham chiếu đến user (tài khoản đăng nhập)

    @Column(name = "specialty")
    String specialty; // Chuyên khoa (Nội, Ngoại, Tai Mũi Họng...)

    @Column(name = "description")
    String description; // Mô tả chi tiết về bác sĩ

    @Column(name = "work_schedule")
    String workSchedule; // Lịch làm việc cơ bản (ví dụ: T2-T6, 8h-17h)
}

