package com.firisbe.securepay.controllers;


import com.firisbe.securepay.entities.Payment;
import com.firisbe.securepay.requests.PaymentRequest;
import com.firisbe.securepay.responses.PaymentResponse;
import com.firisbe.securepay.services.PaymentService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping()
    public ResponseEntity<PaymentResponse> getAllPayments(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
            Page<Payment> payments = paymentService.getAllPayments(page, size);

            PaymentResponse paymentResponse = new PaymentResponse(payments.getContent(), page, size, payments.getTotalPages(), payments.getTotalElements());

            return ResponseEntity.ok(paymentResponse);

    }

    @GetMapping("/between-dates")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentsBetweenDates(
            @Parameter(description = "StartDate", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "EndDate", example = "2024-12-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

            List<Payment> payments = paymentService.getPaymentsBetweenDates(startDate, endDate);

            return ResponseEntity.ok(payments);

    }

    @GetMapping("/monthly-payments")
    @PreAuthorize("hasAuthority('ADMIN') or @customerService.getOneCustomerById(#customerId)?.username == authentication.name")
    public ResponseEntity<List<Payment>> getMonthlyPayments(@RequestParam Long customerId,
                                                @RequestParam int year,
                                                @RequestParam int month) {

            List<Payment> monthlyPayments = paymentService.getMonthlyPaymentsByCustomerId(customerId, year, month);

            return ResponseEntity.ok(monthlyPayments);

    }

    @PostMapping("/make-payment")
    @PreAuthorize("hasAuthority('ADMIN') or @customerService.getOneCustomerById(#paymentRequest.getCustomerId())?.username == authentication.name")
    public ResponseEntity<Payment> makePayment(@Valid @RequestBody PaymentRequest paymentRequest) {

            Payment payment = paymentService.makePayment(paymentRequest.getCustomerId(), paymentRequest.getAmount());

            return ResponseEntity.ok(payment);

    }
}
