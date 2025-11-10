package com.hospital.backend.service;

import com.hospital.backend.dto.request.authentication.LoginRequest;
import com.hospital.backend.dto.request.authentication.SignupRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.User;
import com.hospital.backend.repository.UserRepository;
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

import java.time.LocalDate;
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
            log.info("Login with Google using Keycloak token...");

            // Call endpoint introspection of keycloak to check the token
            String introspectUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(keycloakAccessToken);

            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                    introspectUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            if (!userInfoResponse.getStatusCode().is2xxSuccessful() || userInfoResponse.getBody() == null) {
                return ResponseUtils.buildInternalError("Invalid Keycloak token");
            }

            Map<String, Object> userInfo = userInfoResponse.getBody();
            log.info("Google user info from Keycloak: {}", userInfo);

            String email = (String) userInfo.get("email");
            String username = (String) userInfo.getOrDefault("preferred_username", email);
            Boolean emailVerified = (Boolean) userInfo.getOrDefault("email_verified", false);

            // Save user to DB
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setEmail(email);
                newUser.setPassword("GOOGLE_USER");
                newUser.setLastLoginAt(LocalDate.now());
                return userRepository.save(newUser);
            });

            user.setLastLoginAt(LocalDate.now());
            userRepository.save(user);

            // Response return
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("access_token", keycloakAccessToken);
            data.put("refresh_token", null);
            data.put("expires_in", 300);
            data.put("token_type", "Bearer");

            log.info("Google login successful for {}", email);
            return ResponseUtils.buildSuccessRes(data, "Google login successful");
        } catch (Exception e) {
            log.error("Error during Google login", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
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
            // Get admin token from keycloak
            String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> adminBody = new LinkedMultiValueMap<>();
            adminBody.add("client_id", clientId);
            adminBody.add("client_secret", clientSecret);
            adminBody.add("username", adminUsername);
            adminBody.add("password", adminPassword);
            adminBody.add("grant_type", "password");

            ResponseEntity<Map> tokenResponse =
                    restTemplate.postForEntity(tokenUrl, new HttpEntity<>(adminBody, tokenHeaders), Map.class);

            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to get admin token from Keycloak");
                return ResponseUtils.buildInternalError("Failed to get admin token");
            }

            String adminToken = (String) tokenResponse.getBody().get("access_token");

            // Create user in keycloak
            String createUserUrl = keycloakUrl + "/admin/realms/" + realm + "/users";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            Map<String, Object> userPayload = new HashMap<>();
            userPayload.put("username", request.getUsername());
            userPayload.put("email", request.getEmail());
            userPayload.put("enabled", true);
            userPayload.put("emailVerified", true);

            ResponseEntity<String> createUserRes = restTemplate.postForEntity(
                    createUserUrl, new HttpEntity<>(userPayload, headers), String.class);

            if (!createUserRes.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to create user on Keycloak: {}", createUserRes.getBody());
                return ResponseUtils.buildInternalError("Failed to create user on Keycloak");
            }

            // Get user ID from keycloak
            String userId = extractUserId(request.getUsername(), adminToken);
            if (userId == null) {
                return ResponseUtils.buildInternalError("User ID not found after creation");
            }

            // Assign password
            String setPasswordUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";
            Map<String, Object> passwordPayload = new HashMap<>();
            passwordPayload.put("type", "password");
            passwordPayload.put("value", request.getPassword());
            passwordPayload.put("temporary", false);
            restTemplate.put(setPasswordUrl, new HttpEntity<>(passwordPayload, headers));

            // Assign default role for new user
            assignRoleToUser(adminToken, userId, "ROLE_PATIENT");

            // Save user to DB app
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setEmail(request.getEmail());
            newUser.setLastLoginAt(LocalDate.now());
            userRepository.save(newUser);

            // Get access token
            Map<String, Object> tokenMap = getUserToken(request.getUsername(), request.getPassword());
            if (tokenMap == null) {
                return ResponseUtils.buildInternalError("Failed to get user token after signup");
            }

            // Data return
            Map<String, Object> data = new HashMap<>();
            data.put("user", newUser);
            data.put("access_token", tokenMap.get("access_token"));
            data.put("refresh_token", tokenMap.get("refresh_token"));
            data.put("expires_in", tokenMap.get("expires_in"));
            data.put("token_type", tokenMap.get("token_type"));

            log.info("End signUp in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(data, "User created successfully");
        } catch (Exception e) {
            log.error("System error while signing up user", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
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
            // Lấy role info từ Keycloak
            String roleUrl = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);

            ResponseEntity<Map> roleResponse = restTemplate.exchange(
                    roleUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            if (!roleResponse.getStatusCode().is2xxSuccessful() || roleResponse.getBody() == null) {
                log.error("Failed to get role info for {}", roleName);
                return;
            }

            // Lấy thông tin role từ response
            Map<String, Object> role = roleResponse.getBody();
            Map<String, Object>[] roles = new Map[]{role};

            // Gán role cho user
            String assignUrl = keycloakUrl + "/admin/realms/" + realm +
                    "/users/" + userId + "/role-mappings/realm";

            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(assignUrl, new HttpEntity<>(roles, headers), String.class);
            log.info("Assigned role {} to user {}", roleName, userId);

        } catch (Exception e) {
            log.error("Error assigning role {} to user: {}", roleName, e.getMessage());
        }
    }

}
