package com.hospital.backend.service;

import com.hospital.backend.dto.request.Floor.FloorRequest;
import com.hospital.backend.dto.request.Floor.GetFloorByAreaRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.Floor;
import com.hospital.backend.entity.StaffStatus;
import com.hospital.backend.repository.FloorRepository;
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
public class FloorService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final FloorRepository floorRepository;

    /**
     * Get floor by Area
     */
    @Transactional
    public BaseResponse getFloorByArea(GetFloorByAreaRequest request) {
        log.info("Started fetching all staff statuses");
        long beginTime = System.currentTimeMillis();
        try {
            List<Floor> floorsByArea = floorRepository.findFloorsByAreaId(request.getAreaId());

            log.info("End fetching staff statuses in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(floorsByArea, floorsByArea.size()),
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
}
