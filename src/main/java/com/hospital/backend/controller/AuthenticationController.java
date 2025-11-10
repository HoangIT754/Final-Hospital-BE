package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.authentication.LoginRequest;
import com.hospital.backend.dto.request.authentication.SignupRequest;
import com.hospital.backend.dto.request.room.RoomRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthenticationController {

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping(value = APIConstants.API_LOGIN)
    public ResponseEntity<BaseResponse> login(@RequestBody LoginRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = authService.login(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_LOGIN_GOOGLE)
    public ResponseEntity<BaseResponse> loginWithGoolge(@RequestBody Map<String, String> body) {
        long beginTime = System.currentTimeMillis();
        String token = body.get("token");
        BaseResponse response = authService.loginWithGoogle(token);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_LOGOUT)
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        return authService.logout(refreshToken);
    }

    @PostMapping(value = APIConstants.API_SIGNUP)
    public ResponseEntity<BaseResponse> signup(@RequestBody SignupRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = authService.signUp(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}

