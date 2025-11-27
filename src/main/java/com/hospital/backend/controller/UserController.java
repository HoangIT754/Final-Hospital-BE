package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.UserRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.UserService;
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

    @PostMapping(value = APIConstants.API_COUNT_ALL_ROLES)
    public ResponseEntity<BaseResponse> countAllRoles(){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = userService.countAllRoles();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_USER)
    public ResponseEntity<BaseResponse> getAllUsers(){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = userService.getAllUser();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_USER_BY_ID)
    public ResponseEntity<BaseResponse> getUserById(@RequestBody UserRequest request){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = userService.getUserById(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_UPDATE_USER)
    public ResponseEntity<BaseResponse> updateUser(@RequestBody UserRequest request){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = userService.updateUserProfile(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_USER_BY_USERNAME)
    public ResponseEntity<BaseResponse> getUserByUsername(@RequestBody UserRequest request){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = userService.getUserByUsername(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_USER_PROFILE_BY_ROLE)
    public ResponseEntity<BaseResponse> getUserProfileByRole(@RequestBody UserRequest request){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = userService.getUserProfileByRole(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
