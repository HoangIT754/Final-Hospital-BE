package com.hospital.backend.service;


import com.hospital.backend.dto.commonDto.*;
import com.hospital.backend.dto.request.billing.BillingSummaryRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.billing.BillingSummaryResponse;
import com.hospital.backend.entity.*;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.*;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {
    private static final String FAILED = "failed";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final MedicalRecordRepository medicalRecordRepository;
    private final LabTestOrderRepository labTestOrderRepository;
    private final LabTestOrderDetailRepository labTestOrderDetailRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final InvoiceRepository invoiceRepository;

    @Transactional
    public BaseResponse getBillingSummary(BillingSummaryRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            if (request == null || request.getMedicalRecordId() == null) {
                throw new BadRequestException("medicalRecordId is required");
            }

            UUID medicalRecordId = request.getMedicalRecordId();

            MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                    .orElseThrow(() -> new NotFoundException("Medical record not found"));

            Appointment appointment = medicalRecord.getAppointment();
            if (appointment == null) {
                throw new BadRequestException("Medical record has no appointment");
            }

            // ================== 1. Patient & Doctor info ==================
            PatientProfile patient = appointment.getPatient();
            StaffProfile doctor = appointment.getStaff();

            SimplePatientInfo patientInfo = buildPatientInfo(patient);
            SimpleDoctorInfo doctorInfo = buildDoctorInfo(doctor);

            // ================== 2. Consultation fee (Service fees) ==================
            List<BillingItemLine> serviceLines = new ArrayList<>();
            BigDecimal subtotalServices = BigDecimal.ZERO;

            BigDecimal consultationFee = appointment.getConsultationFee();
            if (consultationFee != null && consultationFee.compareTo(BigDecimal.ZERO) > 0) {

                String desc = appointment.getReason();
                if (desc == null || desc.isBlank()) {
                    desc = "Consultation fee";
                }

                BillingItemLine consultationLine = BillingItemLine.builder()
                        .sourceType("CONSULTATION")
                        .sourceId(appointment.getId())
                        .description(desc)
                        .quantity(1)
                        .unitPrice(consultationFee)
                        .lineTotal(consultationFee)
                        .build();

                serviceLines.add(consultationLine);
                subtotalServices = subtotalServices.add(consultationFee);
            }

            // ================== 3. Lab test fees ==================
            List<BillingItemLine> labLines = new ArrayList<>();
            BigDecimal subtotalLabTests = BigDecimal.ZERO;

            // Lấy tất cả LabTestOrder theo MedicalRecord
            List<LabTestOrder> labOrders =
                    labTestOrderRepository.findByMedicalRecordIdAndIsDeletedFalse(medicalRecordId);

            for (LabTestOrder order : labOrders) {
                if (Boolean.TRUE.equals(order.getIsDeleted())) continue;

                List<LabTestOrderDetail> details =
                        labTestOrderDetailRepository.findByOrderIdAndIsDeletedFalse(order.getId());

                for (LabTestOrderDetail detail : details) {
                    if (Boolean.TRUE.equals(detail.getIsDeleted())) continue;

                    // Tuỳ bạn, ở đây mình chỉ tính phí cho detail COMPLETED
                    if (detail.getStatus() != null &&
                            !"COMPLETED".equalsIgnoreCase(detail.getStatus())) {
                        continue;
                    }

                    LabTest labTest = detail.getLabTest();
                    if (labTest == null) continue;

                    BigDecimal unitPrice = labTest.getPrice() != null
                            ? labTest.getPrice()
                            : BigDecimal.ZERO;
                    int quantity = 1;
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

                    subtotalLabTests = subtotalLabTests.add(lineTotal);

                    labLines.add(
                            BillingItemLine.builder()
                                    .sourceType("LAB_TEST")
                                    .sourceId(detail.getId())
                                    .description(labTest.getName())
                                    .quantity(quantity)
                                    .unitPrice(unitPrice)
                                    .lineTotal(lineTotal)
                                    .build()
                    );
                }
            }

            // ================== 4. Medicine fees (Prescription) ==================
            List<BillingMedicineLine> medicineLines = new ArrayList<>();
            BigDecimal subtotalMedicines = BigDecimal.ZERO;

            Optional<Prescription> prescriptionOpt =
                    prescriptionRepository.findByMedicalRecordIdAndIsDeletedFalse(medicalRecordId);

            if (prescriptionOpt.isPresent()) {
                Prescription prescription = prescriptionOpt.get();

                if (prescription.getItems() != null) {
                    for (PrescriptionItem item : prescription.getItems()) {
                        if (Boolean.TRUE.equals(item.getIsDeleted())) continue;

                        Medicine med = item.getMedicine();
                        if (med == null) continue;

                        int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                        BigDecimal unitPrice = med.getPrice() != null ? med.getPrice() : BigDecimal.ZERO;
                        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

                        subtotalMedicines = subtotalMedicines.add(lineTotal);

                        medicineLines.add(
                                BillingMedicineLine.builder()
                                        .prescriptionId(prescription.getId())
                                        .prescriptionItemId(item.getId())
                                        .medicineId(med.getId())
                                        .medicineName(med.getName())
                                        .quantity(quantity)
                                        .unitPrice(unitPrice)
                                        .lineTotal(lineTotal)
                                        .dosage(item.getDosage())
                                        .frequency(item.getFrequency())
                                        .instruction(item.getInstruction())
                                        .build()
                        );
                    }
                }
            }

            // ================== 5. Tổng tiền ==================
            BigDecimal totalAmount = subtotalServices
                    .add(subtotalLabTests)
                    .add(subtotalMedicines);

            // ================== 6. Invoice info (nếu đã có) ==================
            InvoiceInfo invoiceInfo = null;

            Optional<Invoice> invoiceOpt =
                    invoiceRepository.findFirstByMedicalRecordIdAndIsDeletedFalseOrderByCreateDateDesc(medicalRecordId);

            if (invoiceOpt.isPresent()) {
                Invoice invoice = invoiceOpt.get();
                invoiceInfo = InvoiceInfo.builder()
                        .id(invoice.getId())
                        .code(invoice.getCode())
                        .totalAmount(invoice.getTotalAmount())
                        .status(invoice.getStatus() != null ? invoice.getStatus().name() : null)
                        .currency(invoice.getCurrency())
                        .build();
            }

            // Currency tổng thể: ưu tiên từ Appointment, fallback VND
            String currency = appointment.getConsultationCurrency() != null
                    ? appointment.getConsultationCurrency()
                    : "VND";

            // ================== 7. Build response DTO ==================
            BillingSummaryResponse summary = BillingSummaryResponse.builder()
                    .medicalRecordId(medicalRecord.getId())
                    .appointmentId(appointment.getId())
                    .patient(patientInfo)
                    .doctor(doctorInfo)
                    .services(serviceLines)
                    .labTests(labLines)
                    .medicines(medicineLines)
                    .subtotalServices(subtotalServices)
                    .subtotalLabTests(subtotalLabTests)
                    .subtotalMedicines(subtotalMedicines)
                    .totalAmount(totalAmount)
                    .currency(currency)
                    .invoice(invoiceInfo)
                    .build();

            log.info("End getBillingSummary in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(summary, "Fetched Billing Summary Successfully");

        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error while building billing summary: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while building billing summary", e);
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

    // ================== Helper methods ==================

    private SimplePatientInfo buildPatientInfo(PatientProfile patient) {
        if (patient == null) {
            return SimplePatientInfo.builder().build();
        }

        String firstName = Optional.ofNullable(patient.getFirstName()).orElse("");
        String lastName = Optional.ofNullable(patient.getLastName()).orElse("");
        String fullName = (firstName + " " + lastName).trim();

        String gender = patient.getGender() != null
                ? patient.getGender().toString()
                : null;

        return SimplePatientInfo.builder()
                .id(patient.getId())
                .fullName(fullName)
                .gender(gender)
                .phoneNumber(patient.getPhoneNumber())
                .build();
    }

    private SimpleDoctorInfo buildDoctorInfo(StaffProfile doctor) {
        if (doctor == null) {
            return SimpleDoctorInfo.builder().build();
        }

        String name = null;
        if (doctor.getUser() != null) {
            name = doctor.getUser().getUsername();
        }

        return SimpleDoctorInfo.builder()
                .id(doctor.getId())
                .name(name)
                .build();
    }
}
