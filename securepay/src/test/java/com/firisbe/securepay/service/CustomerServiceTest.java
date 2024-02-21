package com.firisbe.securepay.service;
import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.exceptions.CustomerNotFoundException;
import com.firisbe.securepay.repos.CustomerRepository;
import com.firisbe.securepay.requests.CustomerRequest;
import com.firisbe.securepay.services.CustomerService;
import com.firisbe.securepay.util.CustomerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getAllCustomers() {
        // Mocking repository behavior
        when(customerRepository.findAll()).thenReturn(Arrays.asList(new Customer(), new Customer()));

        // Test logic
        List<Customer> customers = customerService.getAllCustomers();

        // Assertions
        assertNotNull(customers);
        assertEquals(2, customers.size());
    }

    @Test
    void saveOneCustomer() {
        // Mocking repository behavior
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer savedCustomer = invocation.getArgument(0);
            savedCustomer.setId(1L);
            return savedCustomer;
        });

        // Test logic
        Customer newCustomer = new Customer();
        newCustomer.setUsername("testUser");
        newCustomer.setEmail("testuser@example.com");
        newCustomer.setPassword("testPassword");

        Customer savedCustomer = customerService.saveOneCustomer(newCustomer);

        // Assertions
        assertNotNull(savedCustomer.getId());
        assertEquals("encodedPassword", savedCustomer.getPassword());
    }

    @Test
    void getOneCustomerById() {
        // Mocking repository behavior
        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));

        // Test logic
        Customer retrievedCustomer = customerService.getOneCustomerById(1L);

        // Assertions
        assertNotNull(retrievedCustomer);
    }

    @Test
    void updateOneCustomer() {
        // Mocking repository behavior
        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Test logic
        CustomerRequest updatedCustomerRequest = new CustomerRequest();
        updatedCustomerRequest.setUsername("updatedUser");
        updatedCustomerRequest.setEmail("updateduser@example.com");
        updatedCustomerRequest.setPassword("updatedPassword");
        updatedCustomerRequest.setCreditCard("1111-2222-3333-4444");

        // Dönüşümü gerçekleştir
        Customer updatedCustomer = CustomerMapper.customerRequestToCustomer(updatedCustomerRequest);

        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        Customer updatedCustomerResult = customerService.updateOneCustomer(1L, updatedCustomerRequest);

        // Assertions
        assertEquals(updatedCustomerRequest.getUsername(), updatedCustomerResult.getUsername());
        assertEquals(updatedCustomerRequest.getEmail(), updatedCustomerResult.getEmail());
    }

    @Test
    void deleteById() {
        // Mocking repository behavior
        doNothing().when(customerRepository).deleteById(1L);

        // Test logic
        assertDoesNotThrow(() -> customerService.deleteById(1L));
    }

    @Test
    void deleteById_CustomerNotFoundException() {
        // Mocking repository behavior to throw EmptyResultDataAccessException
        doThrow(EmptyResultDataAccessException.class).when(customerRepository).deleteById(1L);

        // Test logic
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteById(1L));
    }

    @Test
    void getOneCustomerByUsername() {
        // Mocking repository behavior
        when(customerRepository.findByUsername("testUser")).thenReturn(new Customer());

        // Test logic
        Customer retrievedCustomer = customerService.getOneCustomerByUsername("testUser");

        // Assertions
        assertNotNull(retrievedCustomer);
    }

    @Test
    void getAdminCount() {
        // Mocking repository behavior
        when(customerRepository.countAdmins()).thenReturn(2L);

        // Test logic
        long adminCount = customerService.getAdminCount();

        // Assertions
        assertEquals(2L, adminCount);
    }
}