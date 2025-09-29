package com.hospital.backend.service;

import com.hospital.backend.dto.request.Role.RoleRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.Role;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.repository.RoleRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final RoleRepository roleRepository;

    /**
     * Create Role
     */
    @Transactional
    public BaseResponse createRole(RoleRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            if (roleRepository.existsByNameIgnoreCase(request.getName())) {
                throw new BadRequestException("Role name already exists");
            }

            Role role = new Role();
            role.setName(request.getName());
            role.setDescription(request.getDescription());
            role.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

            Role savedRole = roleRepository.save(role);

            log.info("End create Role in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(savedRole, "Created Role Successfully");
        } catch (BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation: {}", e.getMessage());
            throw new BadRequestException("Role name must be unique");
        } catch (Exception e) {
            log.error("System error while creating role", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get all Roles
     */
    public BaseResponse getAllRoles() {
        log.info("Started fetching all roles");
        long beginTime = System.currentTimeMillis();
        try {
            List<Role> roles = roleRepository.findAll();
            log.info("End fetching roles in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(roles, roles.size()),
                    "Fetched Roles Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching roles: {}", e.getMessage(), e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }
}
