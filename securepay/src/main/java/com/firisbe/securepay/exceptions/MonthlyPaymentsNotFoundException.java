package com.firisbe.securepay.exceptions;

public class MonthlyPaymentsNotFoundException extends RuntimeException {

    public MonthlyPaymentsNotFoundException(String message) {
        super(message);
    }
}