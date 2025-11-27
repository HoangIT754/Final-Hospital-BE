package com.hospital.backend.dto.response.labTestOrder;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestInOrderDetailResponse {
    UUID labTestId;
    String code;
    String name;
    String description;
    String unit;
    String referenceRange;
    BigDecimal price;
    String currency;

    // từ LabTestOrderDetail
    UUID detailId;
    String result;
    String status;
    String attachmentUrl;
}
