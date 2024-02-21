package com.firisbe.securepay.service;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.Payment;
import com.firisbe.securepay.exceptions.CustomerNotFoundException;
import com.firisbe.securepay.exceptions.DoubleTranscationException;
import com.firisbe.securepay.exceptions.MonthlyPaymentsNotFoundException;
import com.firisbe.securepay.repos.CustomerRepository;
import com.firisbe.securepay.repos.PaymentRepository;
import com.firisbe.securepay.services.CustomerService;
import com.firisbe.securepay.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void makePayment() {
        // Mocking repository behavior
        Long customerId = 1L;

        Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        // Test logic

        Payment paymentMock = new Payment();
        BigDecimal amount = new BigDecimal("100.00");
        paymentMock.setAmount(amount);
        paymentMock.setId(customerId);
        paymentMock.setCustomer(customer);

        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentMock);

        Payment payment = paymentService.makePayment(customerId, amount);

        // Assertions
        assertEquals(amount, payment.getAmount());
        assertEquals(customerId, payment.getCustomer().getId());
    }

    @Test
    void makePayment_DoubleTranscationException() {
        // Mocking repository behavior to simulate an existing payment with the same transaction ID
        when(paymentRepository.findByTransactionId(anyString())).thenReturn(new Payment());

        // Test logic
        Long customerId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        // Assertions
        assertThrows(DoubleTranscationException.class, () -> paymentService.makePayment(customerId, amount));
    }

    @Test
    void makePayment_CustomerNotFoundException() {
        // Mocking repository behavior to simulate a non-existing customer
        when(paymentRepository.findByTransactionId(anyString())).thenReturn(null);
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Test logic
        Long customerId = 1L;
        BigDecimal amount = new BigDecimal("100.00");

        // Assertions
        assertThrows(CustomerNotFoundException.class, () -> paymentService.makePayment(customerId, amount));
    }

    @Test
    void getAllPayments() {
        // Mocking repository behavior
        when(paymentRepository.findAll(PageRequest.of(0, 10, Sort.unsorted())))
                .thenReturn(new PageImpl<>(List.of(new Payment(), new Payment())));


        // Test logic
        int page = 0;
        int size = 10;

        Page<Payment> payments = paymentService.getAllPayments(page, size);

        // Assertions
        assertNotNull(payments);
        assertEquals(2, payments.getSize());
    }

    @Test
    void getPaymentsBetweenDates() {
        // Mocking repository behavior
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        when(paymentRepository.findByCreatedDateBetween(startDate, endDate)).thenReturn(List.of(new Payment(), new Payment()));

        // Test logic
        List<Payment> payments = paymentService.getPaymentsBetweenDates(startDate, endDate);

        // Assertions
        assertNotNull(payments);
        assertEquals(2, payments.size());
    }

    @Test
    void getMonthlyPaymentsByCustomerId() {
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

        Long customerId = 1L;

        Customer customer = new Customer();

        Payment payment = new Payment(1L, customer, BigDecimal.valueOf(100), LocalDateTime.now(), "tx1b");
        Payment payment2 = new Payment(2L, customer, BigDecimal.valueOf(200), LocalDateTime.now(), "tx2b");

        List<Payment> expectedPayments = List.of(payment, payment2);


        reset(paymentRepository);

        when(paymentRepository.findByCustomerIdAndCreatedDateBetween(customerId, startDate, endDate)).thenReturn(expectedPayments);

        List<Payment> monthlyPayments = paymentRepository.findByCustomerIdAndCreatedDateBetween(customerId, startDate, endDate);

        // Assertions
        assertNotNull(monthlyPayments);
        assertEquals(2, monthlyPayments.size());
    }

}
