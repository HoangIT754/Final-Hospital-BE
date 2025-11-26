package com.hospital.backend.dto.response.labTestOrder;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestOrderResponse {
    UUID id;
    String status;
    String testType;
    String orderCode;
    UUID medicalRecordId;
    UUID appointmentId;
    String patientName;
    String doctorName;
    Integer totalTests;
    LocalDateTime createDate;
}
