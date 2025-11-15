package com.hospital.backend.service;

import com.hospital.backend.dto.request.staff.StaffProfileRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.StaffProfile;
import com.hospital.backend.entity.Specialty;
import com.hospital.backend.entity.User;
import com.hospital.backend.entity.WorkSchedule;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.StaffProfileRepository;
import com.hospital.backend.repository.SpecialtyRepository;
import com.hospital.backend.repository.UserRepository;
import com.hospital.backend.repository.WorkScheduleRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StaffProfileService {
    private final StaffProfileRepository staffProfileRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;

    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";
    private final WorkScheduleRepository workScheduleRepository;

    StaffProfileService(StaffProfileRepository staffProfileRepository, UserRepository userRepository, SpecialtyRepository specialtyRepository, WorkScheduleRepository workScheduleRepository) {
        this.staffProfileRepository = staffProfileRepository;
        this.userRepository = userRepository;
        this.specialtyRepository = specialtyRepository;
        this.workScheduleRepository = workScheduleRepository;
    }

    /**
     * Create Staff
     */
    @Transactional
    public BaseResponse createStaff(StaffProfileRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            StaffProfile staff = new StaffProfile();

            // Nếu có userId thì set, nếu không thì bỏ qua
            if (request.getUserId() != null) {
                User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
                staff.setUser(user);
            }

            // Set Specialty (bắt buộc)
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new NotFoundException("Specialty not found"));
            staff.setSpecialty(specialty);

            // WorkSchedule (optional)
            if (request.getWorkScheduleId() != null) {
                WorkSchedule workSchedule = workScheduleRepository.findById(request.getWorkScheduleId())
                        .orElseThrow(() -> new NotFoundException("Work schedule not found"));
                staff.setWorkSchedule(workSchedule);
            }

            // Set các thông tin hồ sơ bác sĩ
            staff.setFirstName(request.getFirstName());
            staff.setLastName(request.getLastName());
            staff.setDateOfBirth(request.getDateOfBirth());
            staff.setGender(request.getGender());
            staff.setAddress(request.getAddress());
            staff.setPhoneNumber(request.getPhoneNumber());
            staff.setDescription(request.getDescription());

            StaffProfile savedDoctor = staffProfileRepository.save(staff);

            log.info("End create Doctor in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(savedDoctor, "Created Doctor Successfully");
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating doctor", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    public StaffProfile getStaffInfoByUsername(StaffProfileRequest request) {
        return staffProfileRepository.findProfileByUsername(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Staff profile not found"));
    }

    public List<StaffProfile> getAllStaffs() {
        return staffProfileRepository.findAll();
    }

    public List<StaffProfile> getAllDoctors() {
        return staffProfileRepository.findAllDoctors();
    }
}
