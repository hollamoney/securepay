package com.firisbe.securepay.util;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.requests.CustomerRequest;
import org.springframework.beans.BeanUtils;

public class CustomerMapper {

    public static Customer customerRequestToCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerRequest, customer);
        return customer;
    }

}