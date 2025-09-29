package com.hospital.backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    String username; // tên đăng nhập

    String password; // mật khẩu

    String email; // email

    Set<UUID> roleIds; // danh sách id role (truyền từ client)
}
