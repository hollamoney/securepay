package com.firisbe.securepay.services;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.exceptions.CustomerNotFoundException;
import com.firisbe.securepay.exceptions.IsExistException;
import com.firisbe.securepay.repos.*;
import com.firisbe.securepay.requests.CustomerRequest;
import com.firisbe.securepay.util.ErrorMessages;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository,PasswordEncoder passwordEncoder
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer saveOneCustomer(Customer newCustomer) {
        if (newCustomer.getPassword() != null && !newCustomer.getPassword().isEmpty()) {
            newCustomer.setPassword(passwordEncoder.encode(newCustomer.getPassword()));
        }
        return customerRepository.save(newCustomer);
    }

    public Customer getOneCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND));
    }

    public Customer updateOneCustomer(Long customerId, CustomerRequest newCustomer) {

        newCustomer.setPassword(passwordEncoder.encode(newCustomer.getPassword()));

        Customer customer = getOneCustomerById(customerId);

        customer.setUsername(newCustomer.getUsername());
        customer.setEmail(newCustomer.getEmail());
        customer.setCreditCard(newCustomer.getCreditCard());
        customer.setPassword(newCustomer.getPassword());

        try {
            return  customerRepository.save(customer);
        } catch (DataIntegrityViolationException ex) {
            throw new IsExistException(ErrorMessages.IS_EXIST);
        }
    }

    public void deleteById(Long customerId) {
        try {
            customerRepository.deleteById(customerId);
        }catch(EmptyResultDataAccessException e) {
            throw new CustomerNotFoundException(ErrorMessages.CUSTOMER_NOT_FOUND);
        }
    }

    public Customer getOneCustomerByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Customer getOneCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public long getAdminCount() {
        return customerRepository.countAdmins();
    }
}
