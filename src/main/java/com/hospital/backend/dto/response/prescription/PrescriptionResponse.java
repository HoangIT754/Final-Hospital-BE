package com.hospital.backend.dto.response.prescription;

import com.hospital.backend.entity.Prescription;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionResponse {
    UUID id;
    String notes;
    Prescription.PrescriptionStatus status;
    LocalDateTime createDate;

    UUID medicalRecordId;
    UUID appointmentId;

    UUID patientId;
    String patientFullName;

    UUID doctorId;
    String doctorFullName;

    List<PrescriptionItemResponse> items;
}
