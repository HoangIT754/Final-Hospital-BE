package com.hospital.backend.dto.response.specialty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecialtyResponse {
    UUID id;
    String name;
    String description;
}
