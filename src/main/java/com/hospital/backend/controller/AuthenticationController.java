package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.authentication.LoginRequest;
import com.hospital.backend.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping(value = APIConstants.API_LOGIN)
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping(value = APIConstants.API_LOGOUT)
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        return authService.logout(refreshToken);
    }

    @PostMapping(value = APIConstants.API_SIGNUP)
    public ResponseEntity<String> signup(@RequestParam String username,
                                         @RequestParam String password,
                                         @RequestParam String email) {
        return authService.signUp(username, password, email);
    }
}

