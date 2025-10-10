package com.hospital.backend.entity;

import com.hospital.backend.constant.GenderEnum;
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
@Table(name = "staff_profile")
public class StaffProfile extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id của hồ sơ bác sĩ

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    User user; // Tham chiếu đến user (tài khoản đăng nhập)

    @Column(name = "first_name", nullable = false)
    String firstName; // tên của bác sĩ

    @Column(name = "last_name", nullable = false)
    String lastName; // họ của bác sĩ

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth; // ngày tháng năm sinh

    @Column(name = "gender")
    GenderEnum gender; // giới tính

    @Column(name = "address")
    String address; // địa chỉ của bác sĩ

    @Column(name = "phone_number", length = 20)
    String phoneNumber; // số điện thoại của bác sĩ

    @ManyToOne
    @JoinColumn(name = "specialty_id", nullable = false)
    Specialty specialty; // Chuyên khoa (Nội, Ngoại, Tai Mũi Họng...)

    @Column(name = "description")
    String description; // Mô tả chi tiết về bác sĩ

    @OneToOne
    @JoinColumn(name = "work_schedule_id")
    private WorkSchedule workSchedule; // Lịch làm việc cơ bản (ví dụ: T2-T6, 8h-17h)

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    private AvailabilityStatus availabilityStatus;

    public enum AvailabilityStatus {
        AVAILABLE, // Rảnh, có thể nhận ca
        BUSY,      // Đang bận
        OFFLINE    // Không trực tuyến
    }

}

