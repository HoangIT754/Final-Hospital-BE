package com.hospital.backend.dto.response.medicalRecord;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordResponse {
    UUID id;
    UUID appointmentId;
    String symptoms;
    String diagnosis;
    String notes;
}
