package com.hospital.backend.dto.response.patient;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientWithAppointmentResponse {
    UUID patientId;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String gender;
    String phoneNumber;
    String identityNumber;
    String emergencyContact;
    String statusCode;
    String statusName;

    UUID appointmentId;
}
