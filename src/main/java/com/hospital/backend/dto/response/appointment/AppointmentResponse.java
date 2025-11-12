package com.hospital.backend.dto.response.appointment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AppointmentResponse {
    private UUID id;
    private String reason;
    private String status;
    private LocalDateTime appointmentStartTime;
    private LocalDateTime appointmentEndTime;

    private UUID patientId;
    private String patientName;

    private UUID staffId;
    private String doctorName;

    private UUID roomId;
    private String roomNo;
}
