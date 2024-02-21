package com.firisbe.securepay.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.firisbe.securepay.controllers.PaymentController;
import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.Payment;
import com.firisbe.securepay.exceptions.DoubleTranscationException;
import com.firisbe.securepay.exceptions.MonthlyPaymentsNotFoundException;
import com.firisbe.securepay.requests.PaymentRequest;
import com.firisbe.securepay.services.PaymentService;
import com.firisbe.securepay.util.ErrorMessages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentController.class)
@WithMockUser(username = "Username")
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("Given a customer details, when the customer is updated, then the creditCard are succesfully updated")
    void givenPaymentsEndpoint_whenGettingPayment_thenReturnCustomerList() throws Exception{
        // Arrange
        String transactionId = UUID.randomUUID().toString();
        Customer customer = new Customer();
        Pageable pageable = Mockito.mock(Pageable.class);

        Payment payment = new Payment(1L,customer, BigDecimal.valueOf(100), LocalDateTime.now(),transactionId);
        Payment payment2 = new Payment(2L,customer,BigDecimal.valueOf(200), LocalDateTime.now(),transactionId);

        List<Payment> expectedPayments = List.of(payment,payment2);

        Page<Payment> paymentPage = new PageImpl<>(expectedPayments, pageable, expectedPayments.size());

        when(paymentService.getAllPayments(0, 10)).thenReturn(paymentPage);

        // Act & Assert
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments", hasSize(expectedPayments.size())))
                .andExpect(jsonPath("$.payments[0].amount", is(payment.getAmount().intValue())))
                .andExpect(jsonPath("$.payments[1].amount", is(payment2.getAmount().intValue())));
    }

    @Test
    @DisplayName("Given start and end dates, when getting payments between dates, then return a list of payments")
    void givenDates_whenGettingPaymentsBetweenDates_thenReturnListOfPayments() throws Exception {
        // Arrange
        LocalDateTime startDate = LocalDateTime.parse("2024-01-01T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2024-12-01T00:00:00");

        List<Payment> expectedPayments = Arrays.asList(
                new Payment(1L, new Customer(), BigDecimal.valueOf(100), LocalDateTime.parse("2024-02-01T12:00:00"), "txn1"),
                new Payment(2L, new Customer(), BigDecimal.valueOf(150), LocalDateTime.parse("2024-06-15T08:30:00"), "txn2")
        );

        when(paymentService.getPaymentsBetweenDates(startDate, endDate)).thenReturn(expectedPayments);

        // Act & Assert
        mockMvc.perform(get("/payments/between-dates")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedPayments.size())))
                .andExpect(jsonPath("$[0].amount").value(expectedPayments.get(0).getAmount()))
                .andExpect(jsonPath("$[1].amount").value(expectedPayments.get(1).getAmount()));
    }

    @Test
    @DisplayName("Given customer ID, year, and month, when getting monthly payments, then return a list of payments")
    void givenValidParams_whenGettingMonthlyPayments_thenReturnListOfPayments() throws Exception {
        // Arrange
        Long customerId = 123L;
        int year = 2024;
        int month = 2;

        List<Payment> expectedPayments = Arrays.asList(
                new Payment(1L, new Customer(), BigDecimal.valueOf(100), LocalDateTime.parse("2024-02-01T12:00:00"), "txn1"),
                new Payment(2L, new Customer(), BigDecimal.valueOf(150), LocalDateTime.parse("2024-02-15T08:30:00"), "txn2")
        );

        when(paymentService.getMonthlyPaymentsByCustomerId(customerId, year, month)).thenReturn(expectedPayments);

        // Act & Assert
        mockMvc.perform(get("/payments/monthly-payments")
                        .param("customerId", String.valueOf(customerId))
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedPayments.size())))
                .andExpect(jsonPath("$[0].amount").value(expectedPayments.get(0).getAmount()))
                .andExpect(jsonPath("$[1].amount").value(expectedPayments.get(1).getAmount()));
    }

    @Test
    @DisplayName("Given non-existent monthly payments, when getting monthly payments, then return 404 Not Found")
    void givenNonExistentMonthlyPayments_whenGettingMonthlyPayments_thenReturnNotFound() throws Exception {
        // Arrange
        Long customerId = 456L;
        int year = 2024;
        int month = 3;

        String message = ErrorMessages.formatMonthlyPaymentsNotFound(customerId, year, month);

        when(paymentService.getMonthlyPaymentsByCustomerId(customerId, year, month)).thenThrow(new MonthlyPaymentsNotFoundException(message));

        // Act & Assert
        mockMvc.perform(get("/payments/monthly-payments")
                        .param("customerId", String.valueOf(customerId))
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(message));
    }

    @Test
    @DisplayName("Given a valid payment request, when making a payment, then return a success message")
    void givenValidPaymentRequest_whenMakingPayment_thenReturnSuccessMessage() throws Exception {
        // Arrange
        PaymentRequest paymentRequest = new PaymentRequest(123L, BigDecimal.valueOf(50));

        Payment mockPayment = new Payment(1L, new Customer(), BigDecimal.valueOf(50), LocalDateTime.now(), "txn123");
        when(paymentService.makePayment(paymentRequest.getCustomerId(), paymentRequest.getAmount())).thenReturn(mockPayment);

        // Act & Assert
        mockMvc.perform(post("/payments/make-payment")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());
        verify(paymentService, times(1)).makePayment(paymentRequest.getCustomerId(), paymentRequest.getAmount());
    }

    @Test
    @DisplayName("Given a invalid payment request, when making a payment, then return throw Exception")
    void givenInvalidPaymentRequest_whenMakingPayment_thenReturnThrowException() throws Exception {
        // Arrange
        PaymentRequest paymentRequest = new PaymentRequest(123L, BigDecimal.valueOf(50));

        Payment mockPayment = new Payment(1L, new Customer(), BigDecimal.valueOf(50), LocalDateTime.now(), "txn123");

        doThrow(new DoubleTranscationException(ErrorMessages.DOUBLE_SPEND))
                .when(paymentService).makePayment(paymentRequest.getCustomerId(),paymentRequest.getAmount());

        // Act & Assert
        mockMvc.perform(post("/payments/make-payment")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(ErrorMessages.DOUBLE_SPEND));

        verify(paymentService, times(1)).makePayment(paymentRequest.getCustomerId(), paymentRequest.getAmount());
    }



}
