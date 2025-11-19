package com.hospital.backend.dto.response.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserWithProfileResponse {
    UUID id;
    String username;
    String email;
    String avatarUrl;
    Set<String> roles;
    Boolean isDeleleted;

    Object staffProfile;
    Object patientProfile;
}
