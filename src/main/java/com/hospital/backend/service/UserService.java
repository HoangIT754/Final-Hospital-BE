package com.hospital.backend.service;

import com.hospital.backend.dto.request.UserRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.Role;
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

import java.util.Date;
import java.util.Objects;
import java.util.Set;
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

//    @Transactional
//    public BaseResponse updateUserProfile(UserRequest request) {
//        long begin = System.currentTimeMillis();
//        try {
//            User user = userRepository.findById(request.getId())
//                    .orElseThrow(() -> new BadRequestException("User not found"));
//
//            // 1) Validate unique username/email nếu có thay đổi
//            if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
//                if (userRepository.existsByUsernameAndIdNot(request.getUsername(), request.getId())) {
//                    throw new BadRequestException("Username already exists");
//                }
//                user.setUsername(request.getUsername());
//            }
//            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
//                if (userRepository.existsByEmailAndIdNot(request.getEmail(), request.getId())) {
//                    throw new BadRequestException("Email already exists");
//                }
//                user.setEmail(request.getEmail());
//            }
//
//            // 2) Đổi password nếu truyền (không bắt buộc)
//            if (request.getPassword() != null && !request.getPassword().isBlank()) {
//                user.setPassword(passwordEncoder.encode(request.getPassword()));
//            }
//
//            // 3) Update roles nếu có
//            if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
//                Set<Role> roles = roleRepository.findAllById(request.getRoleIds())
//                        .stream().collect(Collectors.toSet());
//                user.setRoles(roles);
//            }
//
//            // 4) Xử lý avatar
//            //   a) Nếu client upload file lên BE -> upload Cloudinary ngay tại đây
//            if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
//                // Xoá ảnh cũ nếu có:
//                String oldUrl = user.getAvatarUrl();
//                String oldPublicId = cloudinaryService.extractPublicIdFromUrl(oldUrl);
//
//                // Tạo folder/publicId ổn định theo userId để overwrite
//                String folder = "avatars/users/" + user.getId();
//                String publicId = "avatar"; // luôn là avatar -> sẽ overwrite
//                String newUrl = cloudinaryService.uploadImage(request.getAvatarFile(), folder, publicId);
//
//                // Ghi URL mới & xoá ảnh cũ (an toàn: nếu same publicId thì destroy không cần thiết)
//                user.setAvatarUrl(newUrl);
//                if (oldPublicId != null && !oldPublicId.equals(folder + "/" + publicId)) {
//                    cloudinaryService.deleteByPublicId(oldPublicId);
//                }
//            }
//
//            //   b) Nếu bạn cho phép client tự upload lên Cloudinary và gửi về avatarUrl:
//            if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
//                // Optionally: có thể xoá ảnh cũ nếu khác publicId
//                String oldUrl = user.getAvatarUrl();
//                String oldPublicId = cloudinaryService.extractPublicIdFromUrl(oldUrl);
//                String newPublicId = cloudinaryService.extractPublicIdFromUrl(request.getAvatarUrl());
//
//                user.setAvatarUrl(request.getAvatarUrl());
//
//                if (oldPublicId != null && !Objects.equals(oldPublicId, newPublicId)) {
//                    cloudinaryService.deleteByPublicId(oldPublicId);
//                }
//            }
//
//            User saved = userRepository.save(user);
//            log.info("End update User {} in {} ms", request.getId(), System.currentTimeMillis() - begin);
//            return ResponseUtils.buildSuccessRes(saved, "Updated User Successfully");
//
//        } catch (BadRequestException e) {
//            log.error("Validation error: {}", e.getMessage());
//            throw e;
//        } catch (DataIntegrityViolationException e) {
//            log.error("DB constraint: {}", e.getMessage());
//            throw new BadRequestException("User data must be unique");
//        } catch (Exception e) {
//            log.error("System error while updating user", e);
//            return new BaseResponse(
//                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
//                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
//            );
//        }
//    }
}
