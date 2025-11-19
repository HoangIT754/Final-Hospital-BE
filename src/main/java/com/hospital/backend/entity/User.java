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
import java.util.HashSet;
import java.util.Set;
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

    @Column(name = "username", nullable = false, unique = true)
    String username; // tên đăng nhập trong hệ thống

    @Column(name = "password")
    String password; // mật khẩu cho tài khoản

    @Column(name = "email", unique = true)
    String email; // email của user

    @Column(name = "last_login_at")
    LocalDate lastLoginAt; // thời điểm đăng nhập cuối cùng của user

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles = new HashSet<>(); // Danh sách role của user

    @Column(name = "avatar_url")
    String avatarUrl; // URL of avatar of user

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private StaffProfile staffProfile;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private PatientProfile patientProfile;
}
