package com.hospital.backend.dto.response.invoice;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponse {

    UUID id;
    String code;

    UUID medicalRecordId;
    UUID patientId;
    String patientName;

    BigDecimal subtotal;
    BigDecimal discountAmount;
    BigDecimal taxAmount;
    BigDecimal totalAmount;
    String currency;

    String status; // UNPAID, PAID...
    String type;   // PHARMACY, LAB, MIXED...

    List<InvoiceItemResponse> items;
}
