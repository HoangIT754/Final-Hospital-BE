package com.hospital.backend.dto.response.area;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AreaResponse {
    UUID id;
    String name;
    String code;
}
