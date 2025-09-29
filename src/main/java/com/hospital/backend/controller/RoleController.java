package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.Role.RoleRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(value = APIConstants.API_CREATE_ROLE)
    public ResponseEntity<BaseResponse> createRole(@RequestBody RoleRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = roleService.createRole(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_ROLES)
    public ResponseEntity<BaseResponse> getAllRole() {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = roleService.getAllRoles();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
