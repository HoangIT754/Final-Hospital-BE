package com.hospital.backend.dto.commonDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimplePatientInfo {
    UUID id;
    String fullName;
    String gender;
    String phoneNumber;
}
