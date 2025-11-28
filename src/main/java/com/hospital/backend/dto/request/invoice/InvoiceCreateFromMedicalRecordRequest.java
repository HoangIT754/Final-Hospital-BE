package com.hospital.backend.dto.request.invoice;

import com.hospital.backend.entity.Invoice;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceCreateFromMedicalRecordRequest {
    private UUID medicalRecordId;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private Invoice.InvoiceType type;
}
