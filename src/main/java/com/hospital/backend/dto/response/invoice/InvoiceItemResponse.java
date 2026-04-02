package com.hospital.backend.dto.response.invoice;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceItemResponse {
    private UUID id;
    private String sourceType;   // CONSULTATION / LAB_TEST / PRESCRIPTION_ITEM
    private UUID sourceId;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
