package com.hospital.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.backend.dto.request.authentication.AssignRoleRequest;
import com.hospital.backend.dto.request.authentication.LoginRequest;
import com.hospital.backend.dto.request.authentication.SignupRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.*;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.repository.*;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PatientStatusRepository patientStatusRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final SpecialtyRepository specialtyRepository;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Transactional
    public BaseResponse login(LoginRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            log.info("User login attempt: {}", request.getUsername());

            // Get admin access token from keyclaok
            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("username", request.getUsername());
            body.add("password", request.getPassword());
            body.add("grant_type", "password");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, entity, Map.class);

            if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null) {
                log.warn("Invalid login for user: {}", request.getUsername());
                return ResponseUtils.buildInternalError("Invalid username or password");
            }

            Map<String, Object> tokenMap = tokenResponse.getBody();

            // Check info with DB app
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseGet(() -> {
                        User u = new User();
                        u.setUsername(request.getUsername());
                        u.setEmail("");
                        u.setPassword("N/A");
                        return userRepository.save(u);
                    });
            user.setLastLoginAt(LocalDate.now());
            userRepository.save(user);

            // Data return
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("access_token", tokenMap.get("access_token"));
            data.put("refresh_token", tokenMap.get("refresh_token"));
            data.put("expires_in", tokenMap.get("expires_in"));
            data.put("token_type", tokenMap.get("token_type"));

            log.info("User {} logged in successfully in {} ms", request.getUsername(),
                    System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(data, "Login successfully");
        } catch (Exception e) {
            log.error("System error while logging in user", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    @Transactional
    public BaseResponse loginWithGoogle(String keycloakAccessToken) {
        long beginTime = System.currentTimeMillis();
        try {
            log.info("Login with Google using Keycloak token (decode JWT, no /userinfo)...");

            // Decode JWT access_token
            String[] parts = keycloakAccessToken.split("\\.");
            if (parts.length < 2) {
                return ResponseUtils.buildInternalError("Invalid JWT token format");
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );
            log.info("Decoded JWT payload: {}", payloadJson);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payloadJson, Map.class);

            String email = (String) claims.get("email");
            String username = (String) claims.getOrDefault("preferred_username", email);
            if (username == null && email == null) {
                return ResponseUtils.buildInternalError("Cannot extract user info from token");
            }

            // Lưu / cập nhật user trong DB
            User user;
            if (email != null) {
                user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username != null ? username : email);
                    newUser.setEmail(email);
                    newUser.setPassword(passwordEncoder.encode("GOOGLE_USER"));
                    newUser.setLastLoginAt(LocalDate.now());
                    return userRepository.save(newUser);
                });
            } else {
                // fallback: không có email, dùng username
                user = userRepository.findByUsername(username).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail("");
                    newUser.setPassword(passwordEncoder.encode("GOOGLE_USER"));
                    newUser.setLastLoginAt(LocalDate.now());
                    return userRepository.save(newUser);
                });
            }

            user.setLastLoginAt(LocalDate.now());
            userRepository.save(user);

            Role patientRole = roleRepository.findByName("PATIENT")
                    .orElseThrow(() -> new BadRequestException("Default role PATIENT not found"));

            if (user.getRoles() == null) {
                user.setRoles(new java.util.HashSet<>());
            }

            boolean hasPatientRole = user.getRoles().stream()
                    .anyMatch(r -> "PATIENT".equalsIgnoreCase(r.getName()));

            if (!hasPatientRole) {
                user.getRoles().add(patientRole);
                user = userRepository.save(user);
            }

            PatientProfile profile = patientProfileRepository.findByUserId(user.getId()).orElse(null);

            if (profile == null) {
                profile = new PatientProfile();
                profile.setUser(user);

                profile.setFirstName("New");
                profile.setLastName("Patient");
                profile.setGender(null);
                profile.setAddress(null);
                profile.setPhoneNumber(null);
                profile.setIdentityNumber(null);
                profile.setHealthInsuranceNumber(null);
                profile.setMedicalHistory(null);
                profile.setAllergies(null);
                profile.setEmergencyContact(null);
                profile.setDateOfBirth(null);

                PatientStatus defaultStatus = patientStatusRepository.findByCode("WAITING")
                        .orElseThrow(() -> new BadRequestException("Default patient status not found"));
                profile.setStatus(defaultStatus);
                profile.setIsDeleted(false);

                patientProfileRepository.save(profile);
            } else if (Boolean.TRUE.equals(profile.getIsDeleted())) {
                profile.setIsDeleted(false);
                patientProfileRepository.save(profile);
            }

            // Trả response cho FE
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("access_token", keycloakAccessToken);
            data.put("refresh_token", null);
            data.put("expires_in", 300);
            data.put("token_type", "Bearer");

            log.info("Google login successful for user {}", user.getUsername());
            return ResponseUtils.buildSuccessRes(data, "Google login successful");
        } catch (Exception e) {
            log.error("Error during Google login", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    @Transactional
    public BaseResponse loginWithGoogleCode(String code, String redirectUri) {
        try {
            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("code", code);
            body.add("redirect_uri", redirectUri);

            ResponseEntity<Map> tokenResponse =
                    restTemplate.postForEntity(tokenUrl, new HttpEntity<>(body, headers), Map.class);

            if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null) {
                return ResponseUtils.buildInternalError("Cannot exchange code for token");
            }

            String accessToken = (String) tokenResponse.getBody().get("access_token");
            if (accessToken == null) {
                return ResponseUtils.buildInternalError("No access_token received from Keycloak");
            }

            return loginWithGoogle(accessToken);
        } catch (Exception e) {
            log.error("Error in loginWithGoogleCode", e);
            return ResponseUtils.buildInternalError("Google login failed");
        }
    }



    public ResponseEntity<String> logout(String refreshToken) {
        String logoutUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + refreshToken;

        return restTemplate.postForEntity(logoutUrl, new HttpEntity<>(body, headers), String.class);
    }

    /**
     * Sign up user for keycloak and DB app
     */
    @Transactional
    public BaseResponse signUp(SignupRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            // Get admin token from Keycloak
            String adminToken = getAdminToken();
            if (adminToken == null) {
                return ResponseUtils.buildInternalError("Failed to get admin token");
            }

            // Create User in Keycloak
            String createUserUrl = keycloakUrl + "/admin/realms/" + realm + "/users";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            Map<String, Object> userPayload = new HashMap<>();
            userPayload.put("username", request.getUsername());
            userPayload.put("email", request.getEmail());
            userPayload.put("enabled", true);
            userPayload.put("emailVerified", true);

            ResponseEntity<String> createUserRes =
                    restTemplate.postForEntity(createUserUrl, new HttpEntity<>(userPayload, headers), String.class);

            if (!createUserRes.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to create user on Keycloak: {}", createUserRes.getBody());
                return ResponseUtils.buildInternalError("Failed to create user on Keycloak");
            }

            // Get Keycloak User ID
            String keycloakUserId = extractUserId(request.getUsername(), adminToken);
            if (keycloakUserId == null) {
                return ResponseUtils.buildInternalError("User ID not found after creation");
            }

            // Set password in Keycloak
            String setPasswordUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId + "/reset-password";

            Map<String, Object> passwordPayload = new HashMap<>();
            passwordPayload.put("type", "password");
            passwordPayload.put("value", request.getPassword());
            passwordPayload.put("temporary", false);

            restTemplate.put(setPasswordUrl, new HttpEntity<>(passwordPayload, headers));

            // Assign default role (ROLE_PATIENT)
            assignRoleToUser(adminToken, keycloakUserId, "ROLE_PATIENT");

            // Create User in DB app
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setEmail(request.getEmail());
            newUser.setLastLoginAt(LocalDate.now());

            // Role PATIENT trong DB
            Role patientRole = roleRepository.findByName("PATIENT")
                    .orElseThrow(() -> new BadRequestException("Default role PATIENT not found"));

            newUser.getRoles().add(patientRole);

            User savedUser = userRepository.save(newUser);

            // Create PatientProfile for new user
            PatientProfile profile = new PatientProfile();
            profile.setUser(savedUser);

            // Default patient info when sign up
            profile.setFirstName("New");
            profile.setLastName("Patient");
            profile.setGender(null);
            profile.setAddress(null);
            profile.setPhoneNumber(null);
            profile.setIdentityNumber(null);
            profile.setHealthInsuranceNumber(null);
            profile.setMedicalHistory(null);
            profile.setAllergies(null);
            profile.setEmergencyContact(null);
            profile.setDateOfBirth(null);

            // Set default patient status
            PatientStatus defaultStatus = patientStatusRepository.findByCode("WAITING")
                    .orElseThrow(() -> new BadRequestException("Default patient status not found"));
            profile.setStatus(defaultStatus);

            patientProfileRepository.save(profile);

            // Get access token for FE
            Map<String, Object> tokenMap = getUserToken(request.getUsername(), request.getPassword());
            if (tokenMap == null) {
                return ResponseUtils.buildInternalError("Failed to retrieve token after signup");
            }

            // Return data
            Map<String, Object> data = new HashMap<>();
            data.put("user", savedUser);
            data.put("patient_profile", profile);
            data.put("access_token", tokenMap.get("access_token"));
            data.put("refresh_token", tokenMap.get("refresh_token"));
            data.put("expires_in", tokenMap.get("expires_in"));
            data.put("token_type", tokenMap.get("token_type"));

            log.info("End signUp in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(data, "User created successfully");

        } catch (Exception e) {
            log.error("System error while signing up user", e);
            return ResponseUtils.buildInternalError("Signup failed");
        }
    }


    private Map<String, Object> getUserToken(String username, String password) {
        try {
            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("username", username);
            body.add("password", password);
            body.add("grant_type", "password");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("Error getting token for new user: {}", e.getMessage());
        }
        return null;
    }

    private String extractUserId(String username, String token) {
        String url = keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Map[]> response =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map[].class);

        if (response.getStatusCode().is2xxSuccessful() &&
                response.getBody() != null && response.getBody().length > 0) {
            return (String) response.getBody()[0].get("id");
        }
        return null;
    }

    private void assignRoleToUser(String adminToken, String userId, String roleName) {
        try {
            // Get role from keycloak
            String roleUrl = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);

            ResponseEntity<Map> roleResponse = restTemplate.exchange(
                    roleUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            if (!roleResponse.getStatusCode().is2xxSuccessful() || roleResponse.getBody() == null) {
                log.error("Failed to get role info for {}", roleName);
                return;
            }

            // Get Role info
            Map<String, Object> role = roleResponse.getBody();
            Map<String, Object>[] roles = new Map[]{role};

            // Assign Role for User
            String assignUrl = keycloakUrl + "/admin/realms/" + realm +
                    "/users/" + userId + "/role-mappings/realm";

            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(assignUrl, new HttpEntity<>(roles, headers), String.class);
            log.info("Assigned role {} to user {}", roleName, userId);

        } catch (Exception e) {
            log.error("Error assigning role {} to user: {}", roleName, e.getMessage());
        }
    }

    @Transactional
    public BaseResponse assignRoleToExistingUser(AssignRoleRequest request) {
        long begin = System.currentTimeMillis();
        try {
            // Get admin token Keycloak
            String adminToken = getAdminToken();
            if (adminToken == null) {
                return ResponseUtils.buildInternalError("Cannot get admin token");
            }

            // Get user in DB
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadRequestException("User not found in DB"));

            String newRoleName = request.getNewRoleName().trim().toUpperCase();
            boolean isPatientRole = newRoleName.equals("PATIENT");

            // Get role in DB
            Role newRole = roleRepository.findByName(newRoleName)
                    .orElseThrow(() -> new BadRequestException("Role not found in DB"));

            // Update role in DB
            user.getRoles().clear();
            user.getRoles().add(newRole);
            userRepository.save(user);

            // Sync roles in Keycloak
            String kcUserId = extractUserId(request.getUsername(), adminToken);
            if (kcUserId == null) {
                return ResponseUtils.buildInternalError("User not found in Keycloak");
            }

            removeAllRolesInKeycloak(adminToken, kcUserId);
            assignRoleToUser(adminToken, kcUserId, "ROLE_" + newRoleName);

            // Handle PatientProfile & StaffProfile
            handleUserProfilesBasedOnRole(user, newRoleName);

            log.info("Updated role {} for username {} in {} ms",
                    newRoleName, request.getUsername(), System.currentTimeMillis() - begin);

            return ResponseUtils.buildSuccessRes(null, "Role updated successfully");

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error assigning role", e);
            return ResponseUtils.buildInternalError("Failed to assign role");
        }
    }


    private String getAdminToken() {
        try {
            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("username", adminUsername);
            body.add("password", adminPassword);
            body.add("grant_type", "password");

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(tokenUrl, new HttpEntity<>(body, headers), Map.class);

            return response.getBody() != null ? (String) response.getBody().get("access_token") : null;
        } catch (Exception e) {
            log.error("Cannot get admin token");
            return null;
        }
    }

    private void removeAllRolesInKeycloak(String adminToken, String userId) {
        try {
            String getRolesUrl = keycloakUrl +
                    "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);

            ResponseEntity<Map[]> assignedRolesResponse =
                    restTemplate.exchange(getRolesUrl, HttpMethod.GET, new HttpEntity<>(headers), Map[].class);

            Map[] assignedRoles = assignedRolesResponse.getBody();
            if (assignedRoles == null || assignedRoles.length == 0) return;

            // Remove all
            String deleteUrl = getRolesUrl;
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(assignedRoles, headers), Void.class);

            log.info("Removed all existing roles in Keycloak for user {}", userId);

        } catch (Exception e) {
            log.error("Failed to remove roles in Keycloak: {}", e.getMessage());
        }
    }

    private void handleUserProfilesBasedOnRole(User user, String newRoleName) {

        boolean isPatient = newRoleName.equals("PATIENT");

        // ==================== PATIENT ROLE ====================
        if (isPatient) {
            // Deactivate staff profile if exists
            StaffProfile staff = staffProfileRepository.findByUserId(user.getId()).orElse(null);
            if (staff != null) {
                staff.setIsDeleted(true);
                staffProfileRepository.save(staff);
            }

            // Check existing patient profile
            PatientProfile patient = patientProfileRepository.findByUserId(user.getId()).orElse(null);
            if (patient != null) {
                patient.setIsDeleted(false);
                patientProfileRepository.save(patient);
            } else {
                // Create new patient profile
                PatientProfile newPatient = new PatientProfile();
                newPatient.setUser(user);
                newPatient.setFirstName("New");
                newPatient.setLastName("Patient");

                PatientStatus defaultStatus = patientStatusRepository.findByCode("WAITING")
                        .orElseThrow(() -> new BadRequestException("Default patient status not found"));

                newPatient.setStatus(defaultStatus);
                newPatient.setIsDeleted(false);

                patientProfileRepository.save(newPatient);
            }
            return;
        }

        // ==================== STAFF ROLE ====================

        // Deactivate patient profile if exists
        PatientProfile patient = patientProfileRepository.findByUserId(user.getId()).orElse(null);
        if (patient != null) {
            patient.setIsDeleted(true);
            patientProfileRepository.save(patient);
        }

        // Check existing staff profile
        StaffProfile staff = staffProfileRepository.findByUserId(user.getId()).orElse(null);
        if (staff != null) {
            staff.setIsDeleted(false);
            staffProfileRepository.save(staff);
        } else {
            // Create new staff profile
            StaffProfile newStaff = new StaffProfile();
            newStaff.setUser(user);
            newStaff.setFirstName("New");
            newStaff.setLastName("Staff");
            newStaff.setAvailabilityStatus(StaffProfile.AvailabilityStatus.AVAILABLE);

            // Default specialty
            Specialty defaultSpecialty =
                    specialtyRepository.findByName("General Internal Medicine")
                            .orElseThrow(() -> new BadRequestException("Default specialty not found"));

            newStaff.setSpecialty(defaultSpecialty);
            newStaff.setIsDeleted(false);

            staffProfileRepository.save(newStaff);
        }
    }
}
