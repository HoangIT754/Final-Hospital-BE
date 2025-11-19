package com.hospital.backend.service;

import com.hospital.backend.constant.GenderEnum;
import com.hospital.backend.dto.request.UserRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.patient.PatientProfileResponse;
import com.hospital.backend.dto.response.staff.StaffProfileResponse;
import com.hospital.backend.dto.response.user.UserResponse;
import com.hospital.backend.dto.response.user.UserWithProfileResponse;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.entity.Role;
import com.hospital.backend.entity.StaffProfile;
import com.hospital.backend.entity.User;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.repository.RoleRepository;
import com.hospital.backend.repository.UserRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CloudinaryService cloudinaryService;
//    private final CloudinaryService cloudinaryService;

    /**
     * Create User
     */
    @Transactional
    public BaseResponse createUser(UserRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BadRequestException("Username already exists");
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already exists");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            Set<Role> roles = roleRepository.findAllById(request.getRoleIds())
                    .stream()
                    .collect(Collectors.toSet());

            user.setRoles(roles);
            User savedUser = userRepository.save(user);

            log.info("End create User in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(savedUser, "Created User Successfully");
        } catch (BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation: {}", e.getMessage());
            throw new BadRequestException("User data must be unique");
        } catch (Exception e) {
            log.error("System error while creating user", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    public BaseResponse countAllRoles() {
        long begin = System.currentTimeMillis();
        try {
            List<Object[]> result = userRepository.countUsersGroupByRole();

            // Convert Object[] → JSON Map
            Map<String, Long> response = new HashMap<>();
            for (Object[] row : result) {
                String roleName = (String) row[0];
                Long total = (Long) row[1];
                response.put(roleName, total);
            }

            log.info("Count all roles in {} ms", System.currentTimeMillis() - begin);
            return ResponseUtils.buildSuccessRes(response, "Count All Roles Successfully");

        } catch (Exception e) {
            log.error("Error countAllRoles", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    public BaseResponse getAllUser() {
        long begin = System.currentTimeMillis();

        try {
            List<User> users = userRepository.findAll();

            List<UserWithProfileResponse> response = users.stream()
                    .map(this::mapUser)
                    .collect(Collectors.toList());

            log.info("Get all users in {} ms", System.currentTimeMillis() - begin);
            return ResponseUtils.buildSuccessRes(response, "Get All Users Successfully");

        } catch (Exception e) {
            log.error("Error getAllUser", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    @Transactional
    public BaseResponse updateUserProfile(UserRequest request) {
        long begin = System.currentTimeMillis();

        try {
            // 1) Tìm user
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            // 2) Update username + email nếu khác
            if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
                if (userRepository.existsByUsernameAndIdNot(request.getUsername(), request.getId())) {
                    throw new BadRequestException("Username already exists");
                }
                user.setUsername(request.getUsername());
            }

            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                if (userRepository.existsByEmailAndIdNot(request.getEmail(), request.getId())) {
                    throw new BadRequestException("Email already exists");
                }
                user.setEmail(request.getEmail());
            }

            // 3) Update password nếu có truyền
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            // 4) Update avatar
            if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
                String oldUrl = user.getAvatarUrl();
                String oldPublicId = cloudinaryService.extractPublicIdFromUrl(oldUrl);

                String folder = "avatars/users/" + user.getId();
                String publicId = "avatar";
                String newUrl = cloudinaryService.uploadImage(request.getAvatarFile(), folder, publicId);

                user.setAvatarUrl(newUrl);

                if (oldPublicId != null && !oldPublicId.equals(folder + "/" + publicId)) {
                    cloudinaryService.deleteByPublicId(oldPublicId);
                }
            }

            if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
                String oldUrl = user.getAvatarUrl();
                String oldPublicId = cloudinaryService.extractPublicIdFromUrl(oldUrl);
                String newPublicId = cloudinaryService.extractPublicIdFromUrl(request.getAvatarUrl());

                user.setAvatarUrl(request.getAvatarUrl());

                if (oldPublicId != null && !Objects.equals(oldPublicId, newPublicId)) {
                    cloudinaryService.deleteByPublicId(oldPublicId);
                }
            }

            // Update staff profile
            StaffProfile sp = user.getStaffProfile();
            if (sp != null) {
                if (request.getFirstName() != null) sp.setFirstName(request.getFirstName());
                if (request.getLastName() != null) sp.setLastName(request.getLastName());
                if (request.getAddress() != null) sp.setAddress(request.getAddress());
                if (request.getPhoneNumber() != null) sp.setPhoneNumber(request.getPhoneNumber());
                if (request.getGender() != null && !request.getGender().isBlank()) {
                    try {
                        GenderEnum genderEnum = GenderEnum.valueOf(request.getGender().toUpperCase());
                        sp.setGender(genderEnum);
                    } catch (IllegalArgumentException ex) {
                        log.warn("Invalid gender enum: {}", request.getGender());
                    }
                }
            }

            // Update patient profile
            PatientProfile pp = user.getPatientProfile();
            if (pp != null) {
                if (request.getFirstName() != null) pp.setFirstName(request.getFirstName());
                if (request.getLastName() != null) pp.setLastName(request.getLastName());
                if (request.getAddress() != null) pp.setAddress(request.getAddress());
                if (request.getPhoneNumber() != null) pp.setPhoneNumber(request.getPhoneNumber());
                // Nếu FE có gender thì thêm:
                if (request.getGender() != null) pp.setGender(request.getGender());
            }

            // 7) Save user
            User saved = userRepository.save(user);

            log.info("End update User {} in {} ms", request.getId(), System.currentTimeMillis() - begin);
            return ResponseUtils.buildSuccessRes(saved, "Updated User Successfully");

        } catch (BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while updating user", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }


    private UserWithProfileResponse mapUser(User user) {
        UserWithProfileResponse dto = new UserWithProfileResponse();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setIsDeleleted(user.getIsDeleted());

        dto.setRoles(
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );

        boolean isPatient = dto.getRoles().contains("ROLE_PATIENT");

        // Nếu Patient → trả PatientProfile
        if (isPatient && user.getPatientProfile() != null) {
            PatientProfile pp = user.getPatientProfile();
            PatientProfileResponse resp = new PatientProfileResponse();

            resp.setId(pp.getId());
            resp.setFirstName(pp.getFirstName());
            resp.setLastName(pp.getLastName());
            resp.setPhoneNumber(pp.getPhoneNumber());
            resp.setAddress(pp.getAddress());
            resp.setGender(pp.getGender());

            dto.setPatientProfile(resp);
        }

        // Ngược lại → StaffProfile
        if (!isPatient && user.getStaffProfile() != null) {
            StaffProfile sp = user.getStaffProfile();
            StaffProfileResponse resp = new StaffProfileResponse();

            resp.setId(sp.getId());
            resp.setFirstName(sp.getFirstName());
            resp.setLastName(sp.getLastName());
            resp.setPhoneNumber(sp.getPhoneNumber());
            resp.setAddress(sp.getAddress());
            resp.setDescription(sp.getDescription());
            resp.setGender(sp.getGender() != null ? sp.getGender().name() : null);

            dto.setStaffProfile(resp);
        }
        return dto;
    }

    public BaseResponse getUserById(UserRequest request) {
        long begin = System.currentTimeMillis();

        try {
            User user = userRepository.findById(request.getId())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            UserWithProfileResponse response = mapUser(user);

            log.info("Get user {} in {} ms", request.getId(), System.currentTimeMillis() - begin);
            return ResponseUtils.buildSuccessRes(response, "Get User Successfully");

        } catch (BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error getUserById", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    public BaseResponse getUserByUsername(UserRequest request) {
        long begin = System.currentTimeMillis();

        try {
            if (request.getUsername() == null || request.getUsername().isBlank()) {
                throw new BadRequestException("Username is required");
            }

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadRequestException("User not found"));

            UserWithProfileResponse response = mapUser(user);

            log.info("Get user by username {} in {} ms", request.getUsername(), System.currentTimeMillis() - begin);
            return ResponseUtils.buildSuccessRes(response, "Get User Successfully");

        } catch (BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error getUserByUsername", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

}
