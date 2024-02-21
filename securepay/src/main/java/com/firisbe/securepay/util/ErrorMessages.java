package com.firisbe.securepay.util;

public final class ErrorMessages {

    private ErrorMessages() {}
    public static final String CUSTOMER_NOT_FOUND = "Customer not found.";

    public static final String DOUBLE_SPEND = "Double spend prevention: Payment with the same transactionId already exists.";
    public static final String MONTHLY_PAYMENTS_NOT_FOUND = "Monthly payments not found for customer {customerId} in year {year} and month {month}";

    public static final String IS_EXIST = "Username, email and credit card should be unique.";

    public static String formatMonthlyPaymentsNotFound(Long customerId, int year, int month) {
        String message = MONTHLY_PAYMENTS_NOT_FOUND;
        message = message.replace("{customerId}", String.valueOf(customerId));
        message = message.replace("{year}", String.valueOf(year));
        message = message.replace("{month}", String.valueOf(month));
        return message;
    }
}
