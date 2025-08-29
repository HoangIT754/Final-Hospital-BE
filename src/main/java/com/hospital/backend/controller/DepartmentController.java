package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.department.DepartmentRequest;
import com.hospital.backend.entity.Department;
import com.hospital.backend.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping(APIConstants.API_CREATE_DEPARTMENT)
    public Department createDepartment(@RequestBody DepartmentRequest request) {
        return departmentService.create(request);
    }

    @PostMapping(APIConstants.API_UPDATE_DEPARTMENT)
    public Department updateDepartment(@RequestParam UUID id, @RequestBody DepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @PostMapping(APIConstants.API_DELETE_DEPARTMENT)
    public void deleteDepartment(@RequestParam UUID id) {
        departmentService.delete(id);
    }

    @PostMapping(APIConstants.API_GET_DEPARTMENT_BY_ID)
    public Department getDepartmentById(@RequestParam UUID id) {
        return departmentService.getById(id);
    }

    @PostMapping(APIConstants.API_GET_ALL_DEPARTMENTS)
    public List<Department> getAllDepartments() {
        return departmentService.getAll();
    }
}
