package com.firisbe.securepay.services;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.Payment;
import com.firisbe.securepay.exceptions.CustomerNotFoundException;
import com.firisbe.securepay.exceptions.DoubleTranscationException;
import com.firisbe.securepay.exceptions.MonthlyPaymentsNotFoundException;
import com.firisbe.securepay.repos.CustomerRepository;
import com.firisbe.securepay.repos.PaymentRepository;
import com.firisbe.securepay.util.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Payment makePayment(Long customerId, BigDecimal amount) {
        String transactionId = java.util.UUID.randomUUID().toString();

        Payment existingPayment = paymentRepository.findByTransactionId(transactionId);
        if (existingPayment != null) {
            throw new DoubleTranscationException(ErrorMessages.DOUBLE_SPEND);
        }

        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND));

        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setAmount(amount);
        payment.setTransactionId(transactionId);

        return paymentRepository.save(payment);

    }

    public Page<Payment> getAllPayments(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return paymentRepository.findAll(pageRequest);
    }

    public List<Payment> getPaymentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByCreatedDateBetween(startDate, endDate);
    }

    public List<Payment> getMonthlyPaymentsByCustomerId(Long customerId, int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month + 1, 1, 0, 0);

        List<Payment> monthlyPayments = paymentRepository.findByCustomerIdAndCreatedDateBetween(customerId, startDate, endDate);

        if (monthlyPayments.isEmpty()) {
            throw new MonthlyPaymentsNotFoundException(ErrorMessages.formatMonthlyPaymentsNotFound(customerId, year, month));
        }

        return monthlyPayments;
    }
}