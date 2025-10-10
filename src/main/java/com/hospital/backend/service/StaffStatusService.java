package com.hospital.backend.service;

import com.hospital.backend.dto.request.staffStatus.StaffStatusRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.StaffStatus;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.StaffStatusRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffStatusService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final StaffStatusRepository staffStatusRepository;

    /**
     * Create StaffStatus
     */
    @Transactional
    public BaseResponse createStaffStatus(StaffStatusRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            StaffStatus staffStatus = new StaffStatus();
            staffStatus.setCode(request.getCode());
            staffStatus.setName(request.getName());
            staffStatus.setDescription(request.getDescription());
            StaffStatus saved = staffStatusRepository.save(staffStatus);

            log.info("End create StaffStatus in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(saved, "Created Staff Status Successfully");
        } catch (Exception e) {
            log.error("System error while creating StaffStatus", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get All StaffStatus (chỉ lấy chưa xóa)
     */
    public BaseResponse getAllStaffStatus() {
        log.info("Started fetching all staff statuses");
        long beginTime = System.currentTimeMillis();
        try {
            List<StaffStatus> statuses = staffStatusRepository.findByIsDeletedFalse();

            log.info("End fetching staff statuses in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(statuses, statuses.size()),
                    "Fetched Staff Statuses Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching staff statuses: {}", e.getMessage(), e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Delete StaffStatus = set isDeleted = true
     */
    @Transactional
    public BaseResponse deleteStaffStatus(StaffStatusRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            StaffStatus status = staffStatusRepository.findById(request.getId())
                    .orElseThrow(() -> new NotFoundException("StaffStatus not found with id: " + request.getId()));

            status.setIsDeleted(true);
            staffStatusRepository.save(status);

            log.info("End delete StaffStatus in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(null, "Deleted Staff Status Successfully");
        } catch (NotFoundException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while deleting StaffStatus", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }
}
