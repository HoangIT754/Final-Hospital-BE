package com.hospital.backend.dto.request.staffStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffStatusRequest {
    UUID id;
    String code;
    String name;
    String description;
}
