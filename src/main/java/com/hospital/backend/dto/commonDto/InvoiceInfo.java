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
public class InvoiceInfo {
    UUID id;
    String code;
    BigDecimal totalAmount;
    String status;   // UNPAID, PAID, ...
    String currency;
}
