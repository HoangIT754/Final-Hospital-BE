package com.hospital.backend.dto.request.labTestOrder;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestOrderSearchRequest {
    String startDate;
    String endDate;
    String patientId;
    String doctorId;
    String status;
    String testType;
    String orderCode;
}
