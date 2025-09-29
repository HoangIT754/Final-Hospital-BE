package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.UserRequest;
import com.hospital.backend.dto.request.doctor.DoctorProfileRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.User;
import com.hospital.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = APIConstants.API_CREATE_USER)
    public ResponseEntity<BaseResponse> createUser(@RequestBody UserRequest request){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = userService.createUser(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
