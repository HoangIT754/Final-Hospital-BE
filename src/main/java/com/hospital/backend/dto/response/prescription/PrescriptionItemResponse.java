package com.hospital.backend.dto.response.prescription;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionItemResponse {
    UUID id;
    UUID medicineId;
    String medicineName;

    String dosage;
    String frequency;
    Integer durationDays;
    Integer quantity;
    String route;
    String instruction;
}
