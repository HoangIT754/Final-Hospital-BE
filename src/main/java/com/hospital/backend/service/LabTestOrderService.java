package com.hospital.backend.service;

import com.hospital.backend.dto.request.labTestOrder.LabTestOrderCreateRequest;
import com.hospital.backend.dto.request.labTestOrder.LabTestOrderRequest;
import com.hospital.backend.dto.request.labTestOrder.UpdateLabTestOrderDetailWithFileRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.dto.response.labTestOrder.LabTestInOrderDetailResponse;
import com.hospital.backend.dto.response.labTestOrder.LabTestOrderResponse;
import com.hospital.backend.entity.LabTest;
import com.hospital.backend.entity.LabTestOrder;
import com.hospital.backend.entity.LabTestOrderDetail;
import com.hospital.backend.entity.MedicalRecord;
import com.hospital.backend.entity.Service;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.LabTestOrderDetailRepository;
import com.hospital.backend.repository.LabTestOrderRepository;
import com.hospital.backend.repository.LabTestRepository;
import com.hospital.backend.repository.MedicalRecordRepository;
import com.hospital.backend.repository.ServiceRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Service
@Slf4j
@RequiredArgsConstructor
public class LabTestOrderService {

    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final LabTestOrderRepository labTestOrderRepository;
    private final LabTestOrderDetailRepository labTestOrderDetailRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final ServiceRepository serviceRepository;
    private final LabTestRepository labTestRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public BaseResponse createLabTestOrder(LabTestOrderCreateRequest request) {
        long beginTime = System.currentTimeMillis();
        log.info("Start create LabTestOrder for medicalRecordId={}", request.getMedicalRecordId());

        try {
            // 1. Get MedicalRecord
            MedicalRecord medicalRecord = medicalRecordRepository.findById(request.getMedicalRecordId())
                    .orElseThrow(() -> new NotFoundException("MedicalRecord not found"));

            // 2. Collect LabTests from Services + extra LabTests
            Set<LabTest> labTests = new HashSet<>();

            if (request.getServiceIds() != null && !request.getServiceIds().isEmpty()) {
                List<Service> services = serviceRepository.findAllById(request.getServiceIds());
                if (services.isEmpty()) {
                    throw new NotFoundException("No Service found for given IDs");
                }

                services.forEach(svc -> {
                    if (svc.getLabTests() != null) {
                        labTests.addAll(svc.getLabTests());
                    }
                });
            }

            if (request.getLabTestIds() != null && !request.getLabTestIds().isEmpty()) {
                List<LabTest> extraLabTests = labTestRepository.findAllById(request.getLabTestIds());
                if (extraLabTests.isEmpty()) {
                    log.warn("No extra LabTests found for given labTestIds");
                }
                labTests.addAll(extraLabTests);
            }

            if (labTests.isEmpty()) {
                throw new IllegalArgumentException("LabTest list is empty. Please select at least one Service or LabTest.");
            }

            // 3. Create LabTestOrder (header)
            LabTestOrder order = new LabTestOrder();
            order.setMedicalRecord(medicalRecord);
            order.setTestType(request.getTestType());
            order.setStatus("PENDING");
            order.setOrderCode(generateOrderCode());

            LabTestOrder savedOrder = labTestOrderRepository.save(order);

            // 4. Create details
            List<LabTestOrderDetail> details = labTests.stream()
                    .map(labTest -> {
                        LabTestOrderDetail detail = new LabTestOrderDetail();
                        detail.setOrder(savedOrder);
                        detail.setLabTest(labTest);
                        detail.setStatus("PENDING");
                        detail.setResult(null);
                        return detail;
                    })
                    .collect(Collectors.toList());

            labTestOrderDetailRepository.saveAll(details);

            int totalTests = details.size();

            // 5. Build DTO response
            LabTestOrderResponse responseDto = buildResponseDto(savedOrder, medicalRecord, totalTests);

            log.info("End create LabTestOrder in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(responseDto, "Created LabTestOrder Successfully");
        } catch (NotFoundException | IllegalArgumentException e) {
            log.error("Validation error while creating LabTestOrder: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating LabTestOrder", e);
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

    private LabTestOrderResponse buildResponseDto(LabTestOrder order,
                                                  MedicalRecord medicalRecord,
                                                  Integer totalTests) {
        UUID appointmentId = null;
        String patientName = null;
        String doctorName = null;

        if (medicalRecord != null && medicalRecord.getAppointment() != null) {
            appointmentId = medicalRecord.getAppointment().getId();

            if (medicalRecord.getAppointment().getPatient() != null) {
                var p = medicalRecord.getAppointment().getPatient();
                patientName = (p.getFirstName() != null ? p.getFirstName() : "") + " " +
                        (p.getLastName() != null ? p.getLastName() : "");
            }

            if (medicalRecord.getAppointment().getStaff() != null &&
                    medicalRecord.getAppointment().getStaff().getUser() != null) {
                doctorName = medicalRecord.getAppointment().getStaff().getUser().getUsername();
            }
        }

        return LabTestOrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .status(order.getStatus())
                .testType(order.getTestType())
                .medicalRecordId(medicalRecord != null ? medicalRecord.getId() : null)
                .appointmentId(appointmentId)
                .patientName(patientName)
                .doctorName(doctorName)
                .totalTests(totalTests)
                .createDate(order.getCreateDate())
                .build();
    }

    /**
     * Get all LabTestOrders (trả về dạng DTO)
     */
    public BaseResponse getAllLabTestOrders() {
        log.info("Started fetching all LabTestOrders");
        long beginTime = System.currentTimeMillis();

        try {
            List<LabTestOrder> orders = labTestOrderRepository.findAll();

            List<LabTestOrderResponse> dtos = orders.stream()
                    .map(order -> {
                        MedicalRecord mr = order.getMedicalRecord();
                        return buildResponseDto(order, mr, null);
                    })
                    .toList();

            log.info("End fetching LabTestOrders in {} ms",
                    System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(dtos, dtos.size()),
                    "Fetched LabTestOrders Successfully"
            );
        } catch (Exception e) {
            log.error("Error while fetching LabTestOrders", e);
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

    private String generateOrderCode() {
        Long currentMax = labTestOrderRepository.findMaxOrderCodeIndex();
        long next = (currentMax == null ? 1 : currentMax + 1);

        return String.format("OLT-%06d", next);
    }

    public BaseResponse getLabTestOrderById(LabTestOrderRequest request) {
        log.info("Start fetching LabTestOrder by id={}", request.getLabTestOrderId());
        long beginTime = System.currentTimeMillis();

        try {
            // 1. Lấy order
            LabTestOrder order = labTestOrderRepository.findById(request.getLabTestOrderId())
                    .orElseThrow(() -> new NotFoundException("LabTestOrder not found"));

            MedicalRecord medicalRecord = order.getMedicalRecord();

            // 2. Lấy list detail của order
            List<LabTestOrderDetail> details =
                    labTestOrderDetailRepository.findByOrder(order);

            // 3. Build danh sách DTO lab test (gộp LabTest + Detail)
            List<LabTestInOrderDetailResponse> labTestDtos = details.stream()
                    .filter(d -> d.getLabTest() != null)
                    .map(d -> {
                        LabTest lt = d.getLabTest();
                        return LabTestInOrderDetailResponse.builder()
                                .labTestId(lt.getId())
                                .code(lt.getCode())
                                .name(lt.getName())
                                .description(lt.getDescription())
                                .unit(lt.getUnit())
                                .referenceRange(lt.getReferenceRange())
                                .price(lt.getPrice())
                                .currency(lt.getCurrency())

                                .detailId(d.getId())
                                .result(d.getResult())
                                .status(d.getStatus())
                                .attachmentUrl(d.getAttachmentUrl())
                                .build();
                    })
                    .toList();

            // 4. Suy ra danh sách Service từ các LabTest (như cũ)
            Set<Service> services = details.stream()
                    .map(LabTestOrderDetail::getLabTest)
                    .filter(Objects::nonNull)
                    .flatMap(lt -> {
                        Set<Service> svcs = lt.getServices();
                        return svcs != null ? svcs.stream() : Stream.<Service>empty();
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 5. Build DTO order cơ bản (giống list)
            LabTestOrderResponse orderDto = buildResponseDto(
                    order,
                    medicalRecord,
                    details != null ? details.size() : null
            );

            // 6. Gói vào map để FE dễ xài
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("order", orderDto);
            responseData.put("services", services);
            responseData.put("labTests", labTestDtos);

            log.info("End fetching LabTestOrder by id in {} ms",
                    System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    responseData,
                    "Fetched LabTestOrder Successfully"
            );

        } catch (NotFoundException e) {
            log.error("LabTestOrder not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while fetching LabTestOrder by id", e);
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

    @Transactional
    public BaseResponse updateLabTestOrderDetailWithFile(
            UpdateLabTestOrderDetailWithFileRequest request
    ) {
        long beginTime = System.currentTimeMillis();
        log.info("Start update LabTestOrderDetail id={}", request.getDetailId());

        try {
            LabTestOrderDetail detail = labTestOrderDetailRepository.findById(request.getDetailId())
                    .orElseThrow(() -> new NotFoundException("LabTestOrderDetail not found"));

            // Upload file (if any)
            MultipartFile file = (request.getFiles() != null && !request.getFiles().isEmpty()) ? request.getFiles().getFirst() : null;
            String attachmentUrl = detail.getAttachmentUrl();
            String attachmentPublicId = detail.getAttachmentPublicId();

            if (file != null && !file.isEmpty()) {
                // if has existed file -> delete
                if (attachmentPublicId != null) {
                    try {
                        cloudinaryService.deleteByPublicId(attachmentPublicId);
                    } catch (Exception ex) {
                        log.warn("Delete old lab result file failed: {}", ex.getMessage());
                    }
                }

                // Upload new file
                String publicIdHint = "lab_result_" + request.getDetailId();
                String url = cloudinaryService.uploadFile(file, "lab_results", publicIdHint);
                attachmentUrl = url;
                attachmentPublicId = cloudinaryService.extractPublicIdFromUrl(url);
            }

            // Update result & status
            detail.setResult(request.getResult());
            detail.setStatus(request.getStatus());
            detail.setAttachmentUrl(attachmentUrl);
            detail.setAttachmentPublicId(attachmentPublicId);

            labTestOrderDetailRepository.save(detail);

            log.info("End update LabTestOrderDetail in {} ms",
                    System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    detail,
                    "Update LabTestOrderDetail Successfully"
            );
        } catch (NotFoundException e) {
            log.error("LabTestOrderDetail not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while updating LabTestOrderDetail", e);
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
