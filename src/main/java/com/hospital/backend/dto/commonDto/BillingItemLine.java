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
public class BillingItemLine {
    String sourceType;   // "SERVICE", "LAB_TEST"
    UUID sourceId;       // id của Service hoặc LabTestOrderDetail
    String description;  // tên hiển thị
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
}
