package com.hospital.backend.service;

import com.hospital.backend.dto.request.department.DepartmentRequest;
import com.hospital.backend.entity.Department;
import com.hospital.backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public Department create(DepartmentRequest dto) {
        Department department = new Department();
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setDescription(dto.getDescription());
        department.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return departmentRepository.save(department);
    }

    public Department update(UUID id, DepartmentRequest dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setDescription(dto.getDescription());
        if (dto.getIsActive() != null) {
            department.setIsActive(dto.getIsActive());
        }

        return departmentRepository.save(department);
    }

    public void delete(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        department.setIsDeleted(true);
        departmentRepository.save(department);
    }

    public Department getById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public List<Department> getAll() {
        return departmentRepository.findAll()
                .stream()
                .filter(dept -> Boolean.FALSE.equals(dept.getIsDeleted()))
                .toList();
    }
}
