package com.hospital.backend.dto.response.patient;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatientProfileResponse {
    UUID id;
    String firstName;
    String lastName;
    String phoneNumber;
    String address;
    String gender;
}
