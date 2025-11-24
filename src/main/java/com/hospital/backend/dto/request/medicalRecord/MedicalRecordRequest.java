package com.hospital.backend.dto.request.medicalRecord;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordRequest {

    UUID appointmentId;
    String symptoms;
    String diagnosis;
    String notes;
}
