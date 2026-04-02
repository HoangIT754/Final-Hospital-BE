package com.hospital.backend.dto.commonDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingMedicineLine {
    UUID prescriptionId;
    UUID prescriptionItemId;
    UUID medicineId;
    String medicineName;

    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;

    String dosage;
    String frequency;
    String instruction;
}
