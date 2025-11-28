package com.hospital.backend.service;

import com.hospital.backend.dto.request.invoice.InvoiceCreateFromMedicalRecordRequest;
import com.hospital.backend.dto.request.invoice.InvoiceGetByMedicalRecordRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.invoice.InvoiceItemResponse;
import com.hospital.backend.dto.response.invoice.InvoiceResponse;
import com.hospital.backend.entity.*;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.*;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {
    private static final String FAILED = "failed";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final MedicalRecordRepository medicalRecordRepository;
    private final LabTestOrderRepository labTestOrderRepository;
    private final LabTestOrderDetailRepository labTestOrderDetailRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * 1. invoice/create-from-medical-record
     */
    @Transactional
    public BaseResponse createFromMedicalRecord(InvoiceCreateFromMedicalRecordRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            if (request == null || request.getMedicalRecordId() == null) {
                throw new BadRequestException("medicalRecordId is required");
            }
            UUID medicalRecordId = request.getMedicalRecordId();

            // 1. Check MedicalRecord
            MedicalRecord medicalRecord = medicalRecordRepository.findById(medicalRecordId)
                    .orElseThrow(() -> new NotFoundException("Medical record not found"));

            Appointment appointment = medicalRecord.getAppointment();
            if (appointment == null) {
                throw new BadRequestException("Medical record has no appointment");
            }

            PatientProfile patient = appointment.getPatient();
            if (patient == null) {
                throw new BadRequestException("Appointment has no patient");
            }

            // 2. Check existing invoice (idempotent)
            Optional<Invoice> existingOpt =
                    invoiceRepository.findFirstByMedicalRecordIdAndIsDeletedFalseOrderByCreateDateDesc(medicalRecordId);

            if (existingOpt.isPresent() &&
                    existingOpt.get().getStatus() != Invoice.InvoiceStatus.CANCELLED) {

                // Nếu đã có invoice (chưa CANCELLED) thì trả luôn invoice đó
                InvoiceResponse dto = mapToResponse(existingOpt.get());
                return ResponseUtils.buildSuccessRes(dto, "Invoice already exists. Return existing invoice.");
            }

            // 3. Build all lines (consultation + lab + medicines)
            String currency = appointment.getConsultationCurrency() != null
                    ? appointment.getConsultationCurrency()
                    : "VND";

            List<InvoiceItem> invoiceItems = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;

            // 3.1 Consultation fee
            BigDecimal consultationFee = appointment.getConsultationFee();
            if (consultationFee != null && consultationFee.compareTo(BigDecimal.ZERO) > 0) {
                InvoiceItem item = new InvoiceItem();
                item.setSourceType("CONSULTATION");
                item.setSourceId(appointment.getId());
                item.setDescription(
                        appointment.getReason() != null && !appointment.getReason().isBlank()
                                ? appointment.getReason()
                                : "Consultation fee"
                );
                item.setQuantity(1);
                item.setUnitPrice(consultationFee);
                item.setCurrency(currency);
                item.setLineTotal(consultationFee);

                invoiceItems.add(item);
                subtotal = subtotal.add(consultationFee);
            }

            // 3.2 Lab tests: lab_orders + lab_details + lab_test.price
            List<LabTestOrder> labOrders =
                    labTestOrderRepository.findActiveByMedicalRecordId(medicalRecordId);

            for (LabTestOrder order : labOrders) {
                List<LabTestOrderDetail> details =
                        labTestOrderDetailRepository.findActiveByOrderId(order.getId());

                for (LabTestOrderDetail detail : details) {
                    LabTest labTest = detail.getLabTest();
                    if (labTest == null) continue;

                    // Nếu chỉ muốn tính COMPLETED thì uncomment:
                    // if (detail.getStatus() == null ||
                    //     !"COMPLETED".equalsIgnoreCase(detail.getStatus())) continue;

                    BigDecimal unitPrice = labTest.getPrice() != null
                            ? labTest.getPrice()
                            : BigDecimal.ZERO;
                    int quantity = 1;
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

                    InvoiceItem item = new InvoiceItem();
                    item.setSourceType("LAB_TEST");
                    item.setSourceId(detail.getId());
                    item.setDescription(labTest.getName());
                    item.setQuantity(quantity);
                    item.setUnitPrice(unitPrice);
                    item.setCurrency(currency);
                    item.setLineTotal(lineTotal);

                    invoiceItems.add(item);
                    subtotal = subtotal.add(lineTotal);
                }
            }

            // 3.3 Medicines: prescription + items + medicine.price
            Optional<Prescription> prescriptionOpt =
                    prescriptionRepository.findByMedicalRecordIdAndIsDeletedFalse(medicalRecordId);

            if (prescriptionOpt.isPresent()) {
                Prescription prescription = prescriptionOpt.get();
                if (prescription.getItems() != null) {
                    for (PrescriptionItem pItem : prescription.getItems()) {
                        if (Boolean.TRUE.equals(pItem.getIsDeleted())) continue;

                        Medicine med = pItem.getMedicine();
                        if (med == null) continue;

                        int quantity = pItem.getQuantity() != null ? pItem.getQuantity() : 0;
                        BigDecimal unitPrice = med.getPrice() != null ? med.getPrice() : BigDecimal.ZERO;
                        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

                        InvoiceItem item = new InvoiceItem();
                        item.setSourceType("PRESCRIPTION_ITEM");
                        item.setSourceId(pItem.getId());
                        item.setDescription(med.getName());
                        item.setQuantity(quantity);
                        item.setUnitPrice(unitPrice);
                        item.setCurrency(currency);
                        item.setLineTotal(lineTotal);

                        invoiceItems.add(item);
                        subtotal = subtotal.add(lineTotal);
                    }
                }
            }

            // Nếu không có line nào -> không tạo invoice
            if (invoiceItems.isEmpty()) {
                throw new BadRequestException("No billable items (consultation/lab/medicine) to create invoice");
            }

            // 4. Discount, tax, total
            BigDecimal discount = request.getDiscountAmount() != null
                    ? request.getDiscountAmount()
                    : BigDecimal.ZERO;

            BigDecimal tax = request.getTaxAmount() != null
                    ? request.getTaxAmount()
                    : BigDecimal.ZERO;

            BigDecimal totalAmount = subtotal.subtract(discount).add(tax);

            // 5. Build Invoice
            Invoice invoice = new Invoice();
            invoice.setPatient(patient);
            invoice.setMedicalRecord(medicalRecord);

            Invoice.InvoiceType type = request.getType() != null
                    ? request.getType()
                    : detectInvoiceType(invoiceItems);
            invoice.setType(type);

            invoice.setSubtotal(subtotal);
            invoice.setDiscountAmount(discount);
            invoice.setTaxAmount(tax);
            invoice.setTotalAmount(totalAmount);
            invoice.setCurrency(currency);
            invoice.setStatus(Invoice.InvoiceStatus.UNPAID);
            invoice.setIsDeleted(false);

            // set code
            invoice.setCode(generateInvoiceCode());

            // gán invoice cho từng item
            for (InvoiceItem item : invoiceItems) {
                item.setInvoice(invoice);
                item.setIsDeleted(false);
            }
            invoice.setItems(invoiceItems);

            Invoice saved = invoiceRepository.save(invoice);

            InvoiceResponse dto = mapToResponse(saved);

            log.info("End create invoice from medicalRecord {} in {} ms",
                    medicalRecordId, System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(dto, "Created Invoice Successfully");
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error while creating invoice: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating invoice", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }

    /**
     * 2. invoice/get-by-medical-record
     */
    public BaseResponse getByMedicalRecord(InvoiceGetByMedicalRecordRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            if (request == null || request.getMedicalRecordId() == null) {
                throw new BadRequestException("medicalRecordId is required");
            }

            UUID medicalRecordId = request.getMedicalRecordId();

            Invoice invoice = invoiceRepository
                    .findFirstByMedicalRecordIdAndIsDeletedFalseOrderByCreateDateDesc(medicalRecordId)
                    .orElseThrow(() -> new NotFoundException("Invoice not found for medical record"));

            InvoiceResponse dto = mapToResponse(invoice);

            log.info("End get invoice by medicalRecord {} in {} ms",
                    medicalRecordId, System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(dto, "Fetched Invoice Successfully");
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error while fetching invoice: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while fetching invoice", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }

    // =================== Helper ===================

    /**
     * Đoán type invoice dựa trên các item.
     */
    private Invoice.InvoiceType detectInvoiceType(List<InvoiceItem> items) {
        if (items == null || items.isEmpty()) {
            return Invoice.InvoiceType.PHARMACY;
        }

        boolean hasConsult = items.stream()
                .anyMatch(i -> "CONSULTATION".equalsIgnoreCase(i.getSourceType()));
        boolean hasLab = items.stream()
                .anyMatch(i -> "LAB_TEST".equalsIgnoreCase(i.getSourceType()));
        boolean hasMed = items.stream()
                .anyMatch(i -> "PRESCRIPTION_ITEM".equalsIgnoreCase(i.getSourceType()));

        int cnt = (hasConsult ? 1 : 0) + (hasLab ? 1 : 0) + (hasMed ? 1 : 0);
        if (cnt > 1) {
            return Invoice.InvoiceType.MIXED;
        }
        if (hasMed) return Invoice.InvoiceType.PHARMACY;
        if (hasLab) return Invoice.InvoiceType.LAB;
        return Invoice.InvoiceType.CONSULTATION;
    }

    /**
     * Tạo code hoá đơn dạng: INV-2025-0001
     * (dựa trên số lượng invoice trong năm hiện tại)
     */
    private String generateInvoiceCode() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfYear());

        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = lastDay.atTime(23, 59, 59);

        long count = invoiceRepository.countByCreateDateBetween(start, end) + 1;

        return String.format("INV-%d-%04d", today.getYear(), count);
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        if (invoice == null) return null;

        PatientProfile patient = invoice.getPatient();
        String patientName = null;
        UUID patientId = null;
        if (patient != null) {
            patientId = patient.getId();
            String firstName = patient.getFirstName() != null ? patient.getFirstName() : "";
            String lastName = patient.getLastName() != null ? patient.getLastName() : "";
            patientName = (firstName + " " + lastName).trim();
        }

        List<InvoiceItemResponse> itemDtos =
                (invoice.getItems() != null
                        ? invoice.getItems().stream()
                        .filter(i -> !Boolean.TRUE.equals(i.getIsDeleted()))
                        .map(i -> InvoiceItemResponse.builder()
                                .id(i.getId())
                                .sourceType(i.getSourceType())
                                .sourceId(i.getSourceId())
                                .description(i.getDescription())
                                .quantity(i.getQuantity())
                                .unitPrice(i.getUnitPrice())
                                .lineTotal(i.getLineTotal())
                                .build())
                        .toList()
                        : List.of());

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .code(invoice.getCode())
                .medicalRecordId(
                        invoice.getMedicalRecord() != null ? invoice.getMedicalRecord().getId() : null)
                .patientId(patientId)
                .patientName(patientName)
                .subtotal(invoice.getSubtotal())
                .discountAmount(invoice.getDiscountAmount())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .currency(invoice.getCurrency())
                .status(invoice.getStatus() != null ? invoice.getStatus().name() : null)
                .type(invoice.getType() != null ? invoice.getType().name() : null)
                .items(itemDtos)
                .build();
    }
}
