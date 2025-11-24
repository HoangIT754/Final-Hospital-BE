package com.hospital.backend.service;

import com.hospital.backend.dto.request.medicalRecord.MedicalRecordRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.medicalRecord.MedicalRecordResponse;
import com.hospital.backend.entity.Appointment;
import com.hospital.backend.entity.MedicalRecord;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.AppointmentRepository;
import com.hospital.backend.repository.MedicalRecordRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public BaseResponse createOrUpdateMedicalRecord(MedicalRecordRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            if (request.getAppointmentId() == null) {
                throw new BadRequestException("AppointmentId is required");
            }

            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));

            MedicalRecord medicalRecord = medicalRecordRepository
                    .findByAppointment_Id(appointment.getId())
                    .orElse(null);

            boolean isNew = false;
            if (medicalRecord == null) {
                medicalRecord = new MedicalRecord();
                medicalRecord.setAppointment(appointment);
                isNew = true;
            }

            medicalRecord.setSymptoms(request.getSymptoms());
            medicalRecord.setDiagnosis(request.getDiagnosis());
            medicalRecord.setNotes(request.getNotes());

            MedicalRecord saved = medicalRecordRepository.save(medicalRecord);

            MedicalRecordResponse response = MedicalRecordResponse.builder()
                    .id(saved.getId())
                    .appointmentId(saved.getAppointment().getId())
                    .symptoms(saved.getSymptoms())
                    .diagnosis(saved.getDiagnosis())
                    .notes(saved.getNotes())
                    .build();

            log.info("End create/update MedicalRecord in {} ms",
                    System.currentTimeMillis() - beginTime);

            String message = isNew
                    ? "Created MedicalRecord Successfully"
                    : "Updated MedicalRecord Successfully";

            return ResponseUtils.buildSuccessRes(response, message);

        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while create/update medical record", e);
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

    public BaseResponse getMedicalRecordByAppointmentId(MedicalRecordRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            if (request.getAppointmentId() == null) {
                throw new BadRequestException("AppointmentId is required");
            }

            MedicalRecord medicalRecord = medicalRecordRepository
                    .findByAppointment_Id(request.getAppointmentId())
                    .orElseThrow(() -> new NotFoundException("MedicalRecord not found"));

            MedicalRecordResponse response = MedicalRecordResponse.builder()
                    .id(medicalRecord.getId())
                    .appointmentId(medicalRecord.getAppointment().getId())
                    .symptoms(medicalRecord.getSymptoms())
                    .diagnosis(medicalRecord.getDiagnosis())
                    .notes(medicalRecord.getNotes())
                    .build();

            log.info("End fetching MedicalRecord in {} ms",
                    System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    response,
                    "Fetched MedicalRecord Successfully"
            );
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while fetching medical record", e);
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
