package com.hospital.backend.service;

import com.hospital.backend.dto.request.workSchedule.WorkScheduleRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.StaffProfile;
import com.hospital.backend.entity.WorkSchedule;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.repository.StaffProfileRepository;
import com.hospital.backend.repository.WorkScheduleRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkScheduleService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";
    private final StaffProfileRepository staffProfileRepository;
    private final WorkScheduleRepository workScheduleRepository;

    /**
     * Create WorkSchedule
     */
    @Transactional
    public BaseResponse createWorkSchedule(WorkScheduleRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            // check staff
            StaffProfile staff = staffProfileRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new BadRequestException("Staff not found"));

            // build entity
            WorkSchedule workSchedule = new WorkSchedule();
            workSchedule.setStaff(staff);
            workSchedule.setDayOfWeek(request.getDayOfWeek());
            workSchedule.setStartTime(request.getStartTime());
            workSchedule.setEndTime(request.getEndTime());

            WorkSchedule saved = workScheduleRepository.save(workSchedule);

            log.info("End create WorkSchedule in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(saved, "Created WorkSchedule Successfully");
        } catch (BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation: {}", e.getMessage());
            throw new BadRequestException("WorkSchedule data must be unique");
        } catch (Exception e) {
            log.error("System error while creating work schedule", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }
}
