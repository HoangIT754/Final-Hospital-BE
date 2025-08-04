package com.hospital.backend.dto.request.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PatientRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;

    private String identityNumber;

    private String healthInsuranceNumber;

    private String emergencyContact;

    private String medicalHistory;

    private String allergies;
}
