package com.hospital.backend.dto.request.billing;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingSummaryRequest {
    UUID medicalRecordId;
}
