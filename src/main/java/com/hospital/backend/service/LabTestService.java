package com.hospital.backend.service;

import com.hospital.backend.dto.request.labTest.LabTestRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.LabTest;
import com.hospital.backend.repository.LabTestRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabTestService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final LabTestRepository labTestRepository;

    /**
     * Create LabTest
     */
    @Transactional
    public BaseResponse createLabTest(LabTestRequest request) {
        long beginTime = System.currentTimeMillis();
        log.info("Start create LabTest with name={}", request.getName());

        try {
            LabTest labTest = new LabTest();
            labTest.setName(request.getName());
            labTest.setCode(request.getCode());
            labTest.setDescription(request.getDescription());
            labTest.setUnit(request.getUnit());
            labTest.setReferenceRange(request.getReferenceRange());

            if (request.getPrice() == null) {
                throw new IllegalArgumentException("Price is required");
            }

            labTest.setPrice(request.getPrice().setScale(2, RoundingMode.HALF_UP));
            labTest.setCurrency(request.getCurrency() != null ? request.getCurrency() : "VND");

            if (request.getIsActive() != null) {
                labTest.setIsActive(request.getIsActive());
            }

            LabTest saved = labTestRepository.save(labTest);

            log.info("End create LabTest in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(saved, "Created LabTest Successfully");
        } catch (Exception e) {
            log.error("System error while creating lab test", e);
            return new BaseResponse(
                    500,
                    null,
                    SYSTEM_ERROR,
                    FAILED,
                    1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }

    /**
     * Get All LabTests
     */
    public BaseResponse getAllLabTests() {
        log.info("Started fetching all lab tests");
        long beginTime = System.currentTimeMillis();

        try {
            List<LabTest> labTests = labTestRepository.findAll();

            log.info("End fetching lab tests in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(labTests, labTests.size()),
                    "Fetched LabTests Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching lab tests", e);
            return new BaseResponse(
                    500,
                    null,
                    SYSTEM_ERROR,
                    FAILED,
                    1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }
}
