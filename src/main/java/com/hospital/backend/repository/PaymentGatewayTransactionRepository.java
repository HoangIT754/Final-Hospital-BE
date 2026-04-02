package com.hospital.backend.repository;

import com.hospital.backend.entity.PaymentGatewayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentGatewayTransactionRepository extends JpaRepository<PaymentGatewayTransaction, UUID> {
    Optional<PaymentGatewayTransaction> findByInvoiceIdAndMethodAndStatus(
            UUID invoiceId,
            PaymentGatewayTransaction.PaymentMethod method,
            PaymentGatewayTransaction.PaymentStatus status
    );

    Optional<PaymentGatewayTransaction> findById(UUID id);
}
