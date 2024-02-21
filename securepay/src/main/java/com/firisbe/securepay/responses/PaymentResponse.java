package com.firisbe.securepay.responses;

import com.firisbe.securepay.entities.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private List<Payment> payments;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;

    // Constructor, getters, setters

    // ...
}
