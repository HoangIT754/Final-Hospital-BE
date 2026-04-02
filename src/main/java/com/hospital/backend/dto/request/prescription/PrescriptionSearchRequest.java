package com.hospital.backend.dto.request.prescription;

import com.hospital.backend.entity.Prescription;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionSearchRequest {
    Prescription.PrescriptionStatus status;
    UUID medicalRecordId;
    Boolean isDeleted;

    LocalDate fromDate;
    LocalDate toDate;
}
