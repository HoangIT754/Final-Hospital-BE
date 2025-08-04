package com.hospital.backend.dto.request.department;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentRequest {

    @NotBlank
    String name;

    String code;

    String description;

    Boolean isActive;
}
