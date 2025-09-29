package com.hospital.backend.service;

import com.hospital.backend.dto.request.doctor.DoctorProfileRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.DoctorProfile;
import com.hospital.backend.entity.Specialty;
import com.hospital.backend.entity.User;
import com.hospital.backend.entity.WorkSchedule;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.DoctorProfileRepository;
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
public class DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;

    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";
    private final WorkScheduleRepository workScheduleRepository;

    DoctorProfileService(DoctorProfileRepository doctorProfileRepository, UserRepository userRepository, SpecialtyRepository specialtyRepository, WorkScheduleRepository workScheduleRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.userRepository = userRepository;
        this.specialtyRepository = specialtyRepository;
        this.workScheduleRepository = workScheduleRepository;
    }

    /**
     * Create Doctor
     */
    @Transactional
    public BaseResponse createDoctor(DoctorProfileRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            DoctorProfile doctor = new DoctorProfile();

            // Nếu có userId thì set, nếu không thì bỏ qua
            if (request.getUserId() != null) {
                User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
                doctor.setUser(user);
            }

            // Set Specialty (bắt buộc)
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new NotFoundException("Specialty not found"));
            doctor.setSpecialty(specialty);

            // WorkSchedule (optional)
            if (request.getWorkScheduleId() != null) {
                WorkSchedule workSchedule = workScheduleRepository.findById(request.getWorkScheduleId())
                        .orElseThrow(() -> new NotFoundException("Work schedule not found"));
                doctor.setWorkSchedule(workSchedule);
            }

            // Set các thông tin hồ sơ bác sĩ
            doctor.setFirstName(request.getFirstName());
            doctor.setLastName(request.getLastName());
            doctor.setDateOfBirth(request.getDateOfBirth());
            doctor.setGender(request.getGender());
            doctor.setAddress(request.getAddress());
            doctor.setPhoneNumber(request.getPhoneNumber());
            doctor.setDescription(request.getDescription());

            DoctorProfile savedDoctor = doctorProfileRepository.save(doctor);

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

    public List<DoctorProfile> getAllDoctors() {
        return doctorProfileRepository.findAll();
    }
}
