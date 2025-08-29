package com.hospital.backend.service;

import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.entity.User;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.PatientProfileRepository;
import com.hospital.backend.repository.UserRepository;
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
public class PatientService {

    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;

    /**
     * Create Patient
     */
    @Transactional
    public BaseResponse createPatient(PatientRequest request) {
        log.info("Started creating Patient with request: {}", request);
        long beginTime = System.currentTimeMillis();

        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            PatientProfile patient = new PatientProfile();
            patient.setUser(user);
            patient.setIdentityNumber(request.getIdentityNumber());
            patient.setHealthInsuranceNumber(request.getHealthInsuranceNumber());
            patient.setEmergencyContact(request.getEmergencyContact());
            patient.setMedicalHistory(request.getMedicalHistory());
            patient.setAllergies(request.getAllergies());

            PatientProfile savedPatient = patientProfileRepository.save(patient);

            log.info("End create Patient in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(savedPatient, "Created Patient Successfully");
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get All Patients
     */
    public BaseResponse getAllPatients() {
        log.info("Started fetching all patients");
        long beginTime = System.currentTimeMillis();

        try {
            List<PatientProfile> patients = patientProfileRepository.findAll();
            log.info("End fetching patients in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(patients, patients.size()) ,
                    "Fetched Patients Successfully"
            );
        } catch (Exception e) {
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }
}
