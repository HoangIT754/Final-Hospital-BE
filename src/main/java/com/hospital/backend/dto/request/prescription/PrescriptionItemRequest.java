package com.hospital.backend.dto.request.prescription;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionItemRequest {
    UUID medicineId;
    String dosage;
    String frequency;
    Integer durationDays;
    Integer quantity;
    String route;
    String instruction;
}
