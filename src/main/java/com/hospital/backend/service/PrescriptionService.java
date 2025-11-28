package com.hospital.backend.service;

import com.hospital.backend.dto.request.prescription.*;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.dto.response.prescription.PrescriptionItemResponse;
import com.hospital.backend.dto.response.prescription.PrescriptionResponse;
import com.hospital.backend.entity.*;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.MedicalRecordRepository;
import com.hospital.backend.repository.MedicineRepository;
import com.hospital.backend.repository.PrescriptionRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {
    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicineRepository medicineRepository;

    @Transactional
    public BaseResponse createPrescription(PrescriptionCreateRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            if (request.getMedicalRecordId() == null) {
                throw new BadRequestException("medicalRecordId is required");
            }

            MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                    .orElseThrow(() -> new NotFoundException("Medical record not found"));

            Prescription prescription = new Prescription();
            prescription.setMedicalRecord(medicalRecord);
            prescription.setNotes(request.getNotes());
            prescription.setStatus(Prescription.PrescriptionStatus.NEW);
            prescription.setIsDeleted(false);

            List<PrescriptionItem> items = buildItemsFromRequest(request.getItems(), prescription);
            prescription.setItems(items);

            Prescription saved = prescriptionRepository.save(prescription);

            PrescriptionResponse dto = mapToResponse(saved);

            log.info("End create Prescription in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(dto, "Created Prescription Successfully");
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error while creating prescription: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating prescription", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    private List<PrescriptionItem> buildItemsFromRequest(List<PrescriptionItemRequest> itemRequests,
                                                         Prescription prescription) {
        if (itemRequests == null || itemRequests.isEmpty()) {
            return new ArrayList<>();
        }

        Set<UUID> medicineIds = itemRequests.stream()
                .map(PrescriptionItemRequest::getMedicineId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, Medicine> medicineMap = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, m -> m));

        List<PrescriptionItem> items = new ArrayList<>();

        for (PrescriptionItemRequest itemReq : itemRequests) {
            if (itemReq.getMedicineId() == null) {
                throw new BadRequestException("medicineId is required for each item");
            }

            Medicine med = medicineMap.get(itemReq.getMedicineId());
            if (med == null) {
                throw new NotFoundException("Medicine not found with id: " + itemReq.getMedicineId());
            }

            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(prescription);
            item.setMedicine(med);
            item.setDosage(itemReq.getDosage());
            item.setFrequency(itemReq.getFrequency());
            item.setDurationDays(itemReq.getDurationDays());
            item.setQuantity(itemReq.getQuantity());
            item.setRoute(itemReq.getRoute());
            item.setInstruction(itemReq.getInstruction());
            item.setIsDeleted(false);

            items.add(item);
        }

        return items;
    }


    public BaseResponse getAllByIsDeleted(GetAllPrescriptionByIsDeletedRequest request) {
        log.info("Started fetching prescriptions by isDeleted = {}", request.getIsDeleted());
        long beginTime = System.currentTimeMillis();

        try {
            List<Prescription> prescriptions;
            if (request.getIsDeleted() == null) {
                prescriptions = prescriptionRepository.findAll();
            } else {
                prescriptions = prescriptionRepository.findAllByIsDeleted(request.getIsDeleted());
            }

            log.info("End fetching prescriptions in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(prescriptions, prescriptions.size()),
                    "Fetched Prescriptions Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching prescriptions by isDeleted", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }


    @Transactional
    public BaseResponse updatePrescription(PrescriptionUpdateRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            if (request.getId() == null) {
                throw new BadRequestException("Prescription id is required");
            }

            Prescription prescription = prescriptionRepository.findById(request.getId())
                    .orElseThrow(() -> new NotFoundException("Prescription not found"));

            if (request.getNotes() != null) {
                prescription.setNotes(request.getNotes());
            }
            if (request.getStatus() != null) {
                prescription.setStatus(request.getStatus());
            }

            if (request.getItems() != null) {
                List<PrescriptionItem> currentItems = prescription.getItems();
                if (currentItems == null) {
                    currentItems = new ArrayList<>();
                    prescription.setItems(currentItems);
                } else {
                    currentItems.clear();
                }

                List<PrescriptionItem> newItems = buildItemsFromRequest(request.getItems(), prescription);
                currentItems.addAll(newItems);
            }

            Prescription saved = prescriptionRepository.save(prescription);
            PrescriptionResponse dto = mapToResponse(saved);

            log.info("End update Prescription in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(dto, "Updated Prescription Successfully");

        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error while updating prescription: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while updating prescription", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }


    public BaseResponse searchPrescriptions(PrescriptionSearchRequest request) {
        long beginTime = System.currentTimeMillis();
        log.info("Started searching prescriptions");

        try {
            LocalDateTime fromDateTime = LocalDateTime.of(1970, 1, 1, 0, 0);
            LocalDateTime toDateTime = LocalDateTime.of(2100, 1, 1, 0, 0);

            LocalDate fromDate = request.getFromDate();
            LocalDate toDate = request.getToDate();

            if (fromDate != null) {
                fromDateTime = fromDate.atStartOfDay();
            }
            if (toDate != null) {
                toDateTime = toDate.plusDays(1).atStartOfDay();
            }

            List<Prescription> prescriptions = prescriptionRepository.searchPrescriptions(
                    request.getStatus(),
                    request.getMedicalRecordId(),
                    request.getIsDeleted(),
                    fromDateTime,
                    toDateTime
            );

            List<PrescriptionResponse> dtoList = prescriptions.stream()
                    .map(this::mapToResponse)
                    .toList();

            log.info("End searching prescriptions in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(dtoList, dtoList.size()),
                    "Fetched Prescriptions Successfully"
            );
        } catch (Exception e) {
            log.error("Error while searching prescriptions", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    private PrescriptionResponse mapToResponse(Prescription p) {
        if (p == null) return null;

        // Lấy MedicalRecord, Appointment, Patient, Doctor một cách an toàn
        MedicalRecord medicalRecord = p.getMedicalRecord();
        Appointment appointment = medicalRecord != null ? medicalRecord.getAppointment() : null;
        PatientProfile patient = appointment != null ? appointment.getPatient() : null;
        StaffProfile staff = appointment != null ? appointment.getStaff() : null;

        UUID medicalRecordId = medicalRecord != null ? medicalRecord.getId() : null;
        UUID appointmentId = appointment != null ? appointment.getId() : null;
        UUID patientId = patient != null ? patient.getId() : null;
        UUID doctorId = staff != null ? staff.getId() : null;

        String patientFullName = null;
        if (patient != null) {
            patientFullName = (patient.getFirstName() != null ? patient.getFirstName() : "") +
                    " " +
                    (patient.getLastName() != null ? patient.getLastName() : "");
        }

        String doctorFullName = null;
        if (staff != null && staff.getUser() != null) {
            // Tuỳ bạn muốn lấy tên bác sĩ từ user hay staffProfile
            doctorFullName = staff.getUser().getUsername(); // hoặc firstName + lastName nếu có
        }

        List<PrescriptionItemResponse> itemDtos =
                Optional.ofNullable(p.getItems()).orElse(List.of())
                        .stream()
                        .map(item -> PrescriptionItemResponse.builder()
                                .id(item.getId())
                                .medicineId(item.getMedicine() != null ? item.getMedicine().getId() : null)
                                .medicineName(item.getMedicine() != null ? item.getMedicine().getName() : null)
                                .dosage(item.getDosage())
                                .frequency(item.getFrequency())
                                .durationDays(item.getDurationDays())
                                .quantity(item.getQuantity())
                                .route(item.getRoute())
                                .instruction(item.getInstruction())
                                .build()
                        )
                        .toList();

        return PrescriptionResponse.builder()
                .id(p.getId())
                .notes(p.getNotes())
                .status(p.getStatus())
                .createDate(p.getCreateDate())
                .medicalRecordId(medicalRecordId)
                .appointmentId(appointmentId)
                .patientId(patientId)
                .patientFullName(patientFullName)
                .doctorId(doctorId)
                .doctorFullName(doctorFullName)
                .items(itemDtos)
                .build();
    }
}
