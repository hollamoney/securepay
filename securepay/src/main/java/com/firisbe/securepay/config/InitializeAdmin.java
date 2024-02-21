package com.firisbe.securepay.config;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.Role;
import com.firisbe.securepay.services.CustomerService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class InitializeAdmin {

    private final CustomerService customerService;

    public InitializeAdmin(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostConstruct
    public void init() {
        if (customerService.getAdminCount() == 0) {
            Customer admin = createAdminUser();
            customerService.saveOneCustomer(admin);
        }
    }

    private Customer createAdminUser() {
        Customer admin = new Customer();
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setEmail("admin@admin.com");
        admin.setRole(Role.ADMIN);
        return admin;
    }
}
