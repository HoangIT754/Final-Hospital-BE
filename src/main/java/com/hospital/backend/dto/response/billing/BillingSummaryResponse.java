package com.hospital.backend.dto.response.billing;

import com.hospital.backend.dto.commonDto.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingSummaryResponse {
    UUID medicalRecordId;
    UUID appointmentId;

    SimplePatientInfo patient;
    SimpleDoctorInfo doctor;

    List<BillingItemLine> services;     // tiền dịch vụ khám, service fees
    List<BillingItemLine> labTests;     // tiền các lab test
    List<BillingMedicineLine> medicines;// tiền thuốc

    BigDecimal subtotalServices;
    BigDecimal subtotalLabTests;
    BigDecimal subtotalMedicines;

    BigDecimal totalAmount;
    String currency; // VND

    InvoiceInfo invoice;
}
