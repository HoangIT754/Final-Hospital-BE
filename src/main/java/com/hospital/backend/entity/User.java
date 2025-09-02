package com.hospital.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User extends AuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id của user

    @Column(name = "username")
    @NotNull(message = "username must be not null")
    String username; // tên đăng nhập trong hệ thống

    @Column(name = "password")
    String password; // mật khẩu cho tài khoản

    @Column(name = "email")
    String email; // email của user

    @Column(name = "first_name")
    String firstName; // tên

    @Column(name = "last_name")
    String lastName; // họ

    @Column(name = "address")
    String address; // địa chỉ của user

    @Column(name = "phone_number", length = 20)
    String phoneNumber; // số điện thoại của user

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth; // ngày tháng năm sinh của user

    @Column(name = "gender")
    String gender; // giới tính của user

    @Column(name = "role")
    String role; // vai trò của user trong hệ thống

    @Column(name = "last_login_at")
    LocalDate lastLoginAt; // thời điểm đăng nhập cuối cùng của user
}
