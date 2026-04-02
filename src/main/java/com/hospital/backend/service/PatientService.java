package com.hospital.backend.service;

import com.hospital.backend.dto.request.patient.GetPatientByDoctorRequest;
import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.dto.request.patient.PatientSearchRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.dto.response.patient.PatientWithAppointmentResponse;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.entity.PatientStatus;
import com.hospital.backend.entity.User;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.AppointmentRepository;
import com.hospital.backend.repository.PatientProfileRepository;
import com.hospital.backend.repository.PatientStatusRepository;
import com.hospital.backend.repository.UserRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

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
    private final PatientStatusRepository patientStatusRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Create Patient
     */
    @Transactional
    public BaseResponse createPatient(PatientRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            PatientProfile patient = new PatientProfile();

            if (request.getUserId() != null) {
                User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new NotFoundException("User not found"));
                patient.setUser(user);
            }

            patient.setFirstName(request.getFirstName());
            patient.setLastName(request.getLastName());
            patient.setDateOfBirth(request.getDateOfBirth());
            patient.setGender(request.getGender());
            patient.setAddress(request.getAddress());
            patient.setPhoneNumber(request.getPhoneNumber());
            patient.setIdentityNumber(request.getIdentityNumber());
            patient.setHealthInsuranceNumber(request.getHealthInsuranceNumber());
            patient.setEmergencyContact(request.getEmergencyContact());
            patient.setMedicalHistory(request.getMedicalHistory());
            patient.setAllergies(request.getAllergies());

            PatientStatus status = patientStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new NotFoundException("Patient status not found"));
            patient.setStatus(status);

            PatientProfile savedPatient = patientProfileRepository.save(patient);

            log.info("End create Patient in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(savedPatient, "Created Patient Successfully");
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating patient", e);
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

    public BaseResponse getPatientCountByStatus() {
        log.info("Started counting patients by status");
        long beginTime = System.currentTimeMillis();

        try {
            List<Object[]> results = patientProfileRepository.countPatientsGroupByStatus();
            Map<String, Long> responseData = new HashMap<>();

            for (Object[] row : results) {
                UUID statusId = (UUID) row[0];
                Long count = (Long) row[1];

                PatientStatus status = patientStatusRepository.findById(statusId).orElse(null);
                if (status != null) {
                    responseData.put(status.getCode(), count);
                }
            }

            log.info("End counting patients in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(responseData, "Fetched patient count by status successfully");
        } catch (Exception e) {
            log.error("Error while counting patients: {}", e.getMessage(), e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, "Operation failed",
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Search Patients with filters
     */
    public BaseResponse searchPatients(PatientSearchRequest request) {
        try {
            String firstName = request.getFirstName();
            String lastName = request.getLastName();

            if (firstName != null && !firstName.isBlank()) {
                firstName = "%" + firstName.toLowerCase() + "%";
            }
            if (lastName != null && !lastName.isBlank()) {
                lastName = "%" + lastName.toLowerCase() + "%";
            }

            List<PatientProfile> patients = patientProfileRepository.searchPatients(
                    firstName,
                    lastName,
                    request.getIdentityNumber(),
                    request.getPhoneNumber(),
                    request.getGender(),
                    request.getStatus()
            );

            // Nếu có filter theo tuổi
            if (request.getAges() != null && !request.getAges().isEmpty()) {
                Set<Integer> ageSet = request.getAges().stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());

                patients = patients.stream()
                        .filter(p -> {
                            if (p.getDateOfBirth() == null) return false;
                            int age = DateUtils.calculateAge(p.getDateOfBirth());
                            return ageSet.contains(age);
                        })
                        .toList();
            }

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(patients, patients.size()),
                    "Fetched Patients Successfully"
            );
        } catch (Exception e) {
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    public BaseResponse getAllPatientStatus() {
        log.info("Started fetching all patient statuses");
        long beginTime = System.currentTimeMillis();

        try {
            List<PatientStatus> statuses = patientStatusRepository.findAll();

            log.info("End fetching patient statuses in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(statuses, statuses.size()),
                    "Fetched Patient Statuses Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching patient statuses: {}", e.getMessage(), e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    public BaseResponse getPatientsByDoctorId(GetPatientByDoctorRequest request) {
        try {
            List<Object[]> results =
                    appointmentRepository.findPatientsWithAppointmentByDoctorUserId(request.getDoctorId());

            List<PatientWithAppointmentResponse> patients = results.stream()
                    .map(row -> {
                        PatientProfile patient = (PatientProfile) row[0];
                        UUID appointmentId = (UUID) row[1];

                        return PatientWithAppointmentResponse.builder()
                                .patientId(patient.getId())
                                .firstName(patient.getFirstName())
                                .lastName(patient.getLastName())
                                .dateOfBirth(patient.getDateOfBirth())
                                .gender(patient.getGender())
                                .phoneNumber(patient.getPhoneNumber())
                                .identityNumber(patient.getIdentityNumber())
                                .emergencyContact(patient.getEmergencyContact())
                                .statusCode(patient.getStatus() != null ? patient.getStatus().getCode() : null)
                                .statusName(patient.getStatus() != null ? patient.getStatus().getName() : null)
                                .appointmentId(appointmentId)
                                .build();
                    })
                    .toList();

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(patients, patients.size()),
                    "Fetched Patients By Doctor Successfully"
            );

        } catch (Exception e) {
            log.error("Error while fetching patients by doctorId", e);
            return new BaseResponse(
                    500, null, "System Error", "failed", 1, "Operation failed",
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }
}
