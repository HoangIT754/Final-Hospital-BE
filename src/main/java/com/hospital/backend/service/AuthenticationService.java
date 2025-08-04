package com.hospital.backend.service;

import com.hospital.backend.dto.request.authentication.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RestTemplate restTemplate = new RestTemplate();

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

    public ResponseEntity<String> login(LoginRequest request) {
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

        return restTemplate.postForEntity(tokenUrl, entity, String.class);
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

    public ResponseEntity<String> signUp(String username, String password, String email) {
        // 1. Lấy token admin
        String tokenUrl = keycloakUrl + "/realms/master/protocol/openid-connect/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String tokenBody = "client_id=admin-cli" +
                "&username=" + adminUsername +
                "&password=" + adminPassword +
                "&grant_type=password";

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, new HttpEntity<>(tokenBody, tokenHeaders), Map.class);
        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get admin token");
        }

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. Gửi request tạo user
        String createUserUrl = keycloakUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("enabled", true);
        user.put("emailVerified", true);

        // 3. Gửi POST tạo user
        ResponseEntity<String> userResponse = restTemplate.postForEntity(createUserUrl, new HttpEntity<>(user, headers), String.class);
        if (!userResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create user: " + userResponse.getBody());
        }

        // 4. Gán mật khẩu
        String userId = extractUserId(username, accessToken);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User ID not found after creation");
        }

        String setPasswordUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";

        Map<String, Object> passwordPayload = new HashMap<>();
        passwordPayload.put("type", "password");
        passwordPayload.put("value", password);
        passwordPayload.put("temporary", false);

        restTemplate.put(setPasswordUrl, new HttpEntity<>(passwordPayload, headers));

        return ResponseEntity.ok("User created successfully");
    }

    private String extractUserId(String username, String token) {
        String url = keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Map[]> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().length > 0) {
            return (String) response.getBody()[0].get("id");
        }
        return null;
    }
}
