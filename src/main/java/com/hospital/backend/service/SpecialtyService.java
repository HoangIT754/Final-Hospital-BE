package com.hospital.backend.service;

import com.hospital.backend.dto.request.specialty.SpecialtyRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.Specialty;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.SpecialtyRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialtyService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final SpecialtyRepository specialtyRepository;

    /**
     * Create Specialty
     */
    @Transactional
    public BaseResponse createSpecialty(SpecialtyRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            if (specialtyRepository.existsByNameIgnoreCase(request.getName())) {
                throw new BadRequestException("Specialty name already exists");
            }

            Specialty specialty = new Specialty();
            specialty.setName(request.getName());
            specialty.setDescription(request.getDescription());

            Specialty savedSpecialty = specialtyRepository.save(specialty);

            log.info("End create Specialty in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(savedSpecialty, "Created Specialty Successfully");
        } catch (BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation: {}", e.getMessage());
            throw new BadRequestException("Specialty name must be unique");
        } catch (Exception e) {
            log.error("System error while creating specialty", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get all Specialties
     */
    public BaseResponse getAllSpecialties() {
        log.info("Started fetching all specialties");
        long beginTime = System.currentTimeMillis();

        try {
            List<Specialty> specialties = specialtyRepository.findAll();
            log.info("End fetching specialties in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(specialties, specialties.size()),
                    "Fetched Specialties Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching specialties: {}", e.getMessage(), e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get Specialty by Id
     */
    public BaseResponse getSpecialtyById(UUID id) {
        try {
            Specialty specialty = specialtyRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Specialty not found"));

            return ResponseUtils.buildSuccessRes(specialty, "Fetched Specialty Successfully");
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while fetching specialty by id: {}", e.getMessage(), e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }
}
