package com.hospital.backend.dto.request.Role;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    @NotBlank(message = "Role name is required")
    String name; // Tên role

    String description; // Mô tả chi tiết

    Boolean isActive = true; // Trạng thái (default = true)
}

