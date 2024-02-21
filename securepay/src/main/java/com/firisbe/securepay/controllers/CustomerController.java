package com.firisbe.securepay.controllers;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.requests.CustomerRequest;
import com.firisbe.securepay.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Customer>> getCustomers(){
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ADMIN') or @customerService.getOneCustomerById(#customerId)?.username == authentication.name")
    public ResponseEntity<Customer> getOneUser(@PathVariable Long customerId) {
        Customer customer = customerService.getOneCustomerById(customerId);
        return ResponseEntity.ok(customer);

    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ADMIN') or @customerService.getOneCustomerById(#customerId)?.username == authentication.name")
    public ResponseEntity<Customer> updateOneCustomer(@PathVariable Long customerId,@Valid @RequestBody CustomerRequest customerRequest) {
        Customer updatedCustomer = customerService.updateOneCustomer(customerId, customerRequest);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteOneCustomer(@PathVariable Long customerId) {
        customerService.deleteById(customerId);
    }

}
