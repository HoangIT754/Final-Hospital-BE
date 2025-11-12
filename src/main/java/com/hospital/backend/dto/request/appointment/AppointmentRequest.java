package com.hospital.backend.dto.request.appointment;

import com.hospital.backend.entity.Appointment;
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
public class AppointmentRequest {

    UUID appointmentId;

    UUID patientId; // Id của bệnh nhân đặt lịch

    UUID staffId; // Id của bác sĩ khám

    LocalDateTime appointmentStartTime; // Thời gian bắt đầu khám

    LocalDateTime appointmentEndTime; // Thời gian kết thúc khám (có thể null)

    String reason; // Lý do khám bệnh (tùy chọn)

    UUID room;

    Appointment.AppointmentStatus status; // Trạng thái: PENDING, CONFIRMED, COMPLETED, CANCELED, REQUESTED
}