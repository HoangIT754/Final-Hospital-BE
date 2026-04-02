package com.hospital.backend.service;

import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.dto.response.area.AreaResponse;
import com.hospital.backend.entity.Area;
import com.hospital.backend.entity.StaffStatus;
import com.hospital.backend.repository.AreaRepository;
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
public class AreaService {
    private final AreaRepository areaRepository;

    private static final String FAILED = "Failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    @Transactional
    public BaseResponse getAllArea() {
        log.info("Started fetching all area");
        long beginTime = System.currentTimeMillis();
        try {
            List<Area> areas = areaRepository.findByIsDeletedFalse();

            List<AreaResponse> listAreas = areas.stream().map(a -> {
                AreaResponse areaResponse = new AreaResponse();
                areaResponse.setId(a.getId());
                areaResponse.setName(a.getName());
                areaResponse.setCode(a.getCode());

                return areaResponse;
            }).toList();


            log.info("End fetching area in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(listAreas, listAreas.size()),
                    "Fetched Area Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching area: {}", e.getMessage(), e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }
}
