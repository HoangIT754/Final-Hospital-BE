package com.hospital.backend.dto.request.doctor;

import com.hospital.backend.constant.GenderEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorProfileRequest {
    private UUID userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private GenderEnum gender;
    private String address;
    private String phoneNumber;
    private UUID specialtyId;
    private String description;
    private UUID workScheduleId;
}
