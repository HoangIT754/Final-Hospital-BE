package com.hospital.backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    UUID id;

    String username; // tên đăng nhập

    String password; // mật khẩu

    String email; // email

    Set<UUID> roleIds; // danh sách id role (truyền từ client)

    String avatarUrl;

    MultipartFile avatarFile;
}
