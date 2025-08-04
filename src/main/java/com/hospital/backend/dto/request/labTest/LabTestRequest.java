package com.hospital.backend.dto.request.labTest;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestRequest {
    UUID id;
    String name;
    String code;
    String description;
    String unit;
    String referenceRange;
    Double price;
    Boolean isActive;
}
