package com.firisbe.securepay.controller;

import com.firisbe.securepay.controllers.CustomerController;
import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.Payment;
import com.firisbe.securepay.exceptions.CustomerNotFoundException;
import com.firisbe.securepay.requests.CustomerRequest;
import com.firisbe.securepay.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firisbe.securepay.util.ErrorMessages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@WithMockUser(username = "Username")
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;


    @Test
    @DisplayName("Given a customer details, when the customer is updated, then the creditCard are succesfully updated")
    void givenCustomerDetails_whenCustomerIsUpdated_thenDetailsAreUpdated() throws Exception{
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setCreditCard("4444-3333-2222-1002");
        customer.setUsername("test");
        customer.setPassword("123456");
        customer.setEmail("test@test.com");


        when(customerService.updateOneCustomer(eq(customerId), any(CustomerRequest.class))).thenReturn(customer);

        mockMvc.perform(put("/customers/{id}", customerId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditCard", is(customer.getCreditCard())));
    }

    @Test
    @DisplayName("Given a nonexistent customer, when updating the customer, then a CustomerNotFoundException is thrown")
    void givenNonexistentCustomer_whenGettingCustomer_thenThrowException() throws Exception {
        Long nonexistentCustomerId = 9999L;
        CustomerRequest customer = new CustomerRequest();
        customer.setCreditCard("4444-3333-2222-1002");
        customer.setUsername("test");
        customer.setPassword("123456");
        customer.setEmail("test@test.com");


        when(customerService.updateOneCustomer(nonexistentCustomerId,customer))
                .thenThrow(new CustomerNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND));

        mockMvc.perform(put("/customers/{id}",nonexistentCustomerId)
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorMessages.CUSTOMER_NOT_FOUND));

    }

    @Test
    @DisplayName("Given a customer, when deleting the customer, then customer successfully deleted")
    void givenCustomerId_whenGettingCustomer_thenThrowException() throws Exception{
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);

        doNothing().when(customerService).deleteById(customerId);

        mockMvc.perform(delete("/customers/{id}",customerId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(customerService, times(1)).deleteById(customerId);

    }

    @Test
    @DisplayName("Given a nonexist customer, when deleting the customer, then customer throw exceptions")
    void givenNonexistCustomer_whenCustomerIsExist_thenCustomerDeleted() throws Exception{
        Long customerId = 999L;
        Customer customer = new Customer();
        customer.setId(customerId);

        doThrow(new CustomerNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND + "with" + customerId.toString()))
                .when(customerService).deleteById(customerId);

        mockMvc.perform(delete("/customers/{id}",customerId).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorMessages.CUSTOMER_NOT_FOUND + "with" + customerId.toString()));

        verify(customerService, times(1)).deleteById(customerId);

    }

    @Test
    @DisplayName("Given customer endpoint , when getting customer, then returned ticket list")
    void givenCustomersEndpoint_whenGettingCustomer_thenReturnCustomerList() throws Exception{
        Payment payment = new Payment();
        Customer customer = new Customer(1L,"customer","username","123456","user@name.com",
                "1111 2222 3333 4444",List.of(payment),null);
        Customer customer2 = new Customer(2L,"customer2","nameuser","456123","name@user.com",
                "4444 3333 2222 1111",List.of(payment),null);
        List<Customer> customerList = List.of(customer,customer2);

        when(customerService.getAllCustomers()).thenReturn(customerList);

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(customerList.size())))
                .andExpect(jsonPath("$[0].id", is(customer.getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(customer2.getId().intValue())));
    }


    @Test
    @DisplayName("Given a valid customer ID , when getting customer, then returned customer details")
    void givenValidCustomerId_whenGettingCustomer_thenReturnCustomerDetails() throws Exception{
        Payment payment = new Payment();
        Long customerId = 1L;
        Customer customer = new Customer(customerId,"customer","username","123456","user@name.com",
                "1111 2222 3333 4444",List.of(payment),null);

        when(customerService.getOneCustomerById(customerId)).thenReturn(customer);

        mockMvc.perform(get("/customers/{id}",customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerId.intValue())));
    }

    @Test
    @DisplayName("Given a nonexistent customer ID , when getting customer, then a CustomerNotFoundException is thrown")
    void givenNonexistentCustomerId_whenGettingCustomer_thenThrowException() throws Exception{
        Payment payment = new Payment();
        Long customerId = 999L;

        Customer customer = new Customer(customerId,"customer","username","123456","user@name.com",
                "1111 2222 3333 4444",List.of(payment),null);

        when(customerService.getOneCustomerById(customerId)).
                thenThrow(new CustomerNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND));

        mockMvc.perform(get("/customers/{id}",customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ErrorMessages.CUSTOMER_NOT_FOUND));
    }


}
