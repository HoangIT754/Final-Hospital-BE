package com.hospital.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Username is required")
    private String username; // tên đăng nhập

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password; // mật khẩu

    @Email(message = "Invalid email format")
    private String email; // email

    @NotBlank(message = "First name is required")
    private String firstName; // tên

    @NotBlank(message = "Last name is required")
    private String lastName; // họ

    private String address; // địa chỉ

    @NotNull(message = "Phone number is required")
    private String phoneNumber; // số điện thoại (chỉnh sửa tên đúng chính tả)

    private LocalDate dateOfBirth; // ngày sinh

    private String gender; // giới tính (có thể enum sau)

    @NotBlank(message = "Role is required")
    private String role; // vai trò: ADMIN, DOCTOR, PATIENT, RECEPTIONIST, ...

}

