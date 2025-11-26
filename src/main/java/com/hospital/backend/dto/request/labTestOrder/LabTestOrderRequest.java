package com.hospital.backend.dto.request.labTestOrder;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestOrderRequest {
    UUID labTestOrderId;
}
