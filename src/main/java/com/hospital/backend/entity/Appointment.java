package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "appointment")
public class Appointment extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id của lịch hẹn

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @NotNull
    PatientProfile patient; // Bệnh nhân đặt lịch

    @ManyToOne
    @JoinColumn(name = "staff_id")
    @NotNull
    StaffProfile staff; // Bác sĩ khám

    @Column(name = "appointment_start_time")
    LocalDateTime appointmentStartTime; // Thời gian bắt đầu khám

    @Column(name = "appointment_end_time")
    LocalDateTime appointmentEndTime; // Thời gian khám kết thúc (có thể null)

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    AppointmentStatus status; // Trạng thái lịch hẹn

    @Column(name = "reason")
    String reason; // Lý do khám bệnh (nếu có)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    Room room; // Phòng nơi diễn ra cuộc hẹn

    /**
     * Enum lưu trạng thái của lịch hẹn
     */
    public enum AppointmentStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELED,
        REQUESTED
    }
}
