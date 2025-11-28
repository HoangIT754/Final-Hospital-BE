package com.hospital.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.backend.config.MomoProperties;
import com.hospital.backend.dto.request.payment.CashPaymentRequest;
import com.hospital.backend.dto.request.payment.MomoCallbackRequest;
import com.hospital.backend.dto.request.payment.MomoCreatePaymentRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.payment.MomoCreatePaymentResponse;
import com.hospital.backend.entity.*;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.*;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private static final String FAILED = "failed";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final PaymentGatewayTransactionRepository paymentGatewayTransactionRepository;
    private final MomoProperties momoProperties;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public BaseResponse payByCash(CashPaymentRequest request) {
        long beginTime = System.currentTimeMillis();

        try {
            if (request.getInvoiceId() == null) {
                throw new BadRequestException("invoiceId is required");
            }

            Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                    .orElseThrow(() -> new NotFoundException("Invoice not found"));

            if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
                throw new BadRequestException("Invoice already paid");
            }

            if (Boolean.TRUE.equals(invoice.getIsDeleted())) {
                throw new BadRequestException("Invoice is deleted");
            }

            BigDecimal invoiceAmount = invoice.getTotalAmount();
            if (invoiceAmount == null) {
                invoiceAmount = invoice.getSubtotal();
            }
            if (invoiceAmount == null) {
                throw new BadRequestException("Invoice amount is not set");
            }

            BigDecimal payAmount = request.getAmount() != null
                    ? request.getAmount()
                    : invoiceAmount;

            if (payAmount.compareTo(invoiceAmount) < 0) {
                throw new BadRequestException("Paid amount is less than invoice total");
            }

            Set<Prescription> affectedPrescriptions = new HashSet<>();

            if (invoice.getItems() != null) {
                for (InvoiceItem invItem : invoice.getItems()) {
                    if (!"PRESCRIPTION_ITEM".equalsIgnoreCase(invItem.getSourceType())) {
                        continue;
                    }

                    UUID presItemId = invItem.getSourceId();
                    if (presItemId == null) continue;

                    PrescriptionItem pItem = prescriptionItemRepository.findById(presItemId)
                            .orElseThrow(() -> new NotFoundException(
                                    "Prescription item not found: " + presItemId));

                    Prescription prescription = pItem.getPrescription();
                    if (prescription != null) {
                        affectedPrescriptions.add(prescription);
                    }

                    Medicine med = pItem.getMedicine();
                    if (med == null) continue;

                    Integer stock = med.getStock() != null ? med.getStock() : 0;
                    Integer qty = pItem.getQuantity() != null ? pItem.getQuantity() : 0;

                    if (qty > 0) {
                        if (stock < qty) {
                            throw new BadRequestException(
                                    "Not enough stock for medicine: " + med.getName()
                            );
                        }
                        med.setStock(stock - qty);
                        medicineRepository.save(med);
                    }
                }
            }

            for (Prescription p : affectedPrescriptions) {
                p.setStatus(Prescription.PrescriptionStatus.DISPENSED);
                prescriptionRepository.save(p);
            }

            Payment payment = new Payment();
            payment.setInvoice(invoice);
            payment.setAmount(payAmount);
            payment.setCurrency(invoice.getCurrency());
            payment.setMethod(Payment.PaymentMethod.CASH);
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            payment.setIsDeleted(false);

            paymentRepository.save(payment);

            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoiceRepository.save(invoice);

            log.info("Pay by cash for invoice {} in {} ms",
                    invoice.getId(), System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(invoice, "Paid invoice by CASH successfully");

        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error when payByCash: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error when payByCash", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    @Transactional
    public BaseResponse createMomoPayment(MomoCreatePaymentRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            if (request == null || request.getInvoiceId() == null) {
                throw new BadRequestException("invoiceId is required");
            }

            UUID invoiceId = request.getInvoiceId();
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new NotFoundException("Invoice not found"));

            if (invoice.getStatus() == Invoice.InvoiceStatus.PAID ||
                    invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
                throw new BadRequestException("Invoice has invalid status for payment");
            }

            BigDecimal amount = invoice.getTotalAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Invoice totalAmount must be > 0");
            }

            // 1. Tạo PaymentGatewayTransaction
            PaymentGatewayTransaction pg = new PaymentGatewayTransaction();
            pg.setInvoice(invoice);
            pg.setAmount(amount);
            pg.setCurrency(invoice.getCurrency() != null ? invoice.getCurrency() : "VND");
            pg.setMethod(PaymentGatewayTransaction.PaymentMethod.MOMO);
            pg.setStatus(PaymentGatewayTransaction.PaymentStatus.PENDING);
            pg = paymentGatewayTransactionRepository.save(pg);

            // orderId: dùng id transaction cho dễ mapping callback
            String orderId = pg.getId().toString();
            String requestId = UUID.randomUUID().toString();

            long amountLong = amount.longValue(); // giả sử totalAmount là số nguyên VND

            String partnerCode = momoProperties.getPartnerCode();
            String accessKey = momoProperties.getAccessKey();
            String secretKey = momoProperties.getSecretKey();

            String orderInfo = "Hospital invoice " + invoice.getCode();
            String redirectUrl = momoProperties.getRedirectUrl();
            String ipnUrl = momoProperties.getIpnUrl();
            String extraData = ""; // có thể encode invoiceId, userId,...

            // raw signature string theo docs MoMo
            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amountLong +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + ipnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + redirectUrl +
                    "&requestId=" + requestId +
                    "&requestType=captureWallet";

            String signature = hmacSHA256(secretKey, rawSignature);

            Map<String, Object> payload = new HashMap<>();
            payload.put("partnerCode", partnerCode);
            payload.put("accessKey", accessKey);
            payload.put("requestId", requestId);
            payload.put("amount", String.valueOf(amountLong));
            payload.put("orderId", orderId);
            payload.put("orderInfo", orderInfo);
            payload.put("redirectUrl", redirectUrl);
            payload.put("ipnUrl", ipnUrl);
            payload.put("extraData", extraData);
            payload.put("requestType", "captureWallet");
            payload.put("signature", signature);
            payload.put("lang", "vi");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);

            String endpoint = momoProperties.getEndpoint();
            ResponseEntity<String> momoRes = restTemplate.postForEntity(endpoint, httpEntity, String.class);

            if (!momoRes.getStatusCode().is2xxSuccessful() || momoRes.getBody() == null) {
                pg.setStatus(PaymentGatewayTransaction.PaymentStatus.FAILED);
                paymentGatewayTransactionRepository.save(pg);
                throw new RuntimeException("MoMo request failed");
            }

            Map<String, Object> momoBody = objectMapper.readValue(momoRes.getBody(), Map.class);

            Integer resultCode = (Integer) momoBody.get("resultCode");
            if (resultCode == null || resultCode != 0) {
                pg.setStatus(PaymentGatewayTransaction.PaymentStatus.FAILED);
                paymentGatewayTransactionRepository.save(pg);
                String message = (String) momoBody.get("message");
                throw new RuntimeException("MoMo create payment error: " + message);
            }

            String payUrl = (String) momoBody.get("payUrl"); // web
            String deeplink = (String) momoBody.get("deeplink"); // app
            String qrCodeUrl = (String) momoBody.get("qrCodeUrl"); // nếu docs có field này

            // Nếu MoMo không trả qrCodeUrl, FE có thể generate QR từ payUrl

            MomoCreatePaymentResponse dto = MomoCreatePaymentResponse.builder()
                    .orderId(orderId)
                    .requestId(requestId)
                    .payUrl(payUrl != null ? payUrl : deeplink)
                    .qrCodeUrl(qrCodeUrl) // có thể null
                    .build();

            log.info("End create MoMo payment for invoice {} in {} ms",
                    invoiceId, System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(dto, "Create MoMo payment successfully");
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error while creating MoMo payment: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating MoMo payment", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }

    private String hmacSHA256(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKeySpec);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hash.append('0');
            hash.append(hex);
        }
        return hash.toString();
    }

    @Transactional
    public ResponseEntity<String> handleMomoCallback(MomoCallbackRequest callback) {
        try {
            log.info("Received MoMo callback: {}", callback);

            // 1. Verify signature
            String rawSignature = "accessKey=" + momoProperties.getAccessKey() +
                    "&amount=" + callback.getAmount() +
                    "&extraData=" + callback.getExtraData() +
                    "&message=" + callback.getMessage() +
                    "&orderId=" + callback.getOrderId() +
                    "&orderInfo=" + callback.getOrderInfo() +
                    "&orderType=" + callback.getOrderType() +
                    "&partnerCode=" + callback.getPartnerCode() +
                    "&payType=" + callback.getPayType() +
                    "&requestId=" + callback.getRequestId() +
                    "&responseTime=" + callback.getResponseTime() +
                    "&resultCode=" + callback.getResultCode() +
                    "&transId=" + callback.getTransId();

            String expectedSignature = hmacSHA256(momoProperties.getSecretKey(), rawSignature);
            if (!expectedSignature.equals(callback.getSignature())) {
                log.error("Invalid MoMo signature");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid signature");
            }

            // 2. Tìm transaction bằng orderId
            UUID transactionId;
            try {
                transactionId = UUID.fromString(callback.getOrderId());
            } catch (Exception e) {
                log.error("Invalid orderId (not UUID): {}", callback.getOrderId());
                return ResponseEntity.ok("orderId invalid");
            }

            PaymentGatewayTransaction pg = paymentGatewayTransactionRepository.findById(transactionId)
                    .orElse(null);
            if (pg == null) {
                log.error("PaymentGatewayTransaction not found for orderId={}", callback.getOrderId());
                return ResponseEntity.ok("transaction not found");
            }

            // Nếu đã xử lý SUCCESS rồi thì ignore (idempotent)
            if (pg.getStatus() == PaymentGatewayTransaction.PaymentStatus.SUCCESS) {
                return ResponseEntity.ok("already success");
            }

            Invoice invoice = pg.getInvoice();
            if (invoice == null) {
                log.error("Invoice is null in PaymentGatewayTransaction {}", pg.getId());
                return ResponseEntity.ok("invoice not found");
            }

            // 3. ResultCode check
            if (callback.getResultCode() == 0) {
                // SUCCESS
                pg.setStatus(PaymentGatewayTransaction.PaymentStatus.SUCCESS);
                pg.setPaidAt(LocalDateTime.now());
                paymentGatewayTransactionRepository.save(pg);

                // Tạo Payment
                Payment payment = new Payment();
                payment.setInvoice(invoice);
                payment.setAmount(pg.getAmount());
                payment.setCurrency(pg.getCurrency());
                payment.setMethod(Payment.PaymentMethod.MOMO);
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);

                // Cập nhật Invoice
                invoice.setStatus(Invoice.InvoiceStatus.PAID);
                invoiceRepository.save(invoice);

                // Cập nhật Prescription (nếu có)
                MedicalRecord mr = invoice.getMedicalRecord();
                if (mr != null) {
                    Optional<Prescription> presOpt =
                            prescriptionRepository.findByMedicalRecordIdAndIsDeletedFalse(mr.getId());
                    if (presOpt.isPresent()) {
                        Prescription pres = presOpt.get();
                        pres.setStatus(Prescription.PrescriptionStatus.DISPENSED);
                        prescriptionRepository.save(pres);
                    }
                }

                log.info("MoMo payment success for invoice {}", invoice.getId());
                return ResponseEntity.ok("success");
            } else {
                pg.setStatus(PaymentGatewayTransaction.PaymentStatus.FAILED);
                paymentGatewayTransactionRepository.save(pg);

                log.info("MoMo payment failed for invoice {} with resultCode={}",
                        invoice.getId(), callback.getResultCode());
                return ResponseEntity.ok("failed");
            }

        } catch (Exception e) {
            log.error("System error in MoMo callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("error");
        }
    }
}
