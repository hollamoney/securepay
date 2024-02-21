package com.firisbe.securepay.repos;

import com.firisbe.securepay.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByTransactionId(String transactionId);

    List<Payment> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> findByCustomerIdAndCreatedDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}
