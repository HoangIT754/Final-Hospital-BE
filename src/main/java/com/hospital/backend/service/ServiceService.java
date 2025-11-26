package com.hospital.backend.service;

import com.hospital.backend.dto.request.service.ServiceRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.LabTest;
import com.hospital.backend.entity.Service;
import com.hospital.backend.repository.LabTestRepository;
import com.hospital.backend.repository.ServiceRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final ServiceRepository serviceRepository;
    private final LabTestRepository labTestRepository;

    /**
     * Create Service (with optional list of LabTests)
     */
    @Transactional
    public BaseResponse createService(ServiceRequest request) {
        long beginTime = System.currentTimeMillis();
        log.info("Start create Service with name={}", request.getName());

        try {
            Service service = new Service();
            service.setName(request.getName());
            service.setCode(request.getCode());
            service.setDescription(request.getDescription());
            service.setPrice(request.getPrice());
            service.setDurationMinutes(request.getDurationMinutes());

            if (request.getIsActive() != null) {
                service.setIsActive(request.getIsActive());
            }

            Set<LabTest> labTests = new HashSet<>();
            if (request.getLabTestIds() != null && !request.getLabTestIds().isEmpty()) {
                labTests = request.getLabTestIds().stream()
                        .map(this::findLabTestOrThrow)
                        .collect(Collectors.toSet());
            }
            service.setLabTests(labTests);

            Service saved = serviceRepository.save(service);

            log.info("End create Service in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(saved, "Created Service Successfully");
        } catch (EntityNotFoundException e) {
            log.error("Validation error when creating service: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating service", e);
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

    private LabTest findLabTestOrThrow(UUID id) {
        return labTestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LabTest not found with id: " + id));
    }

    /**
     * Get All Services
     */
    public BaseResponse getAllServices() {
        log.info("Started fetching all services");
        long beginTime = System.currentTimeMillis();

        try {
            List<Service> services = serviceRepository.findAll();

            log.info("End fetching services in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(services, services.size()),
                    "Fetched Services Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching services", e);
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
