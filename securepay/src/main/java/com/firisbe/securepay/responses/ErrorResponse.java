package com.firisbe.securepay.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.firisbe.securepay.validation.error.ValidationError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {

    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String message;
    private List<ValidationError> errors;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.errors = new ArrayList<>();
    }

    public void addValidationError(String field, String message) {
        ValidationError error = new ValidationError(field, message);
        errors.add(error);
    }
}
