package com.hospital.backend.dto.request.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PatientRequest {

    private UUID userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String phoneNumber;

    private String identityNumber;

    private String healthInsuranceNumber;

    private String emergencyContact;

    private String medicalHistory;

    private String allergies;

    @NotNull(message = "Status ID is required")
    private UUID statusId;
}
