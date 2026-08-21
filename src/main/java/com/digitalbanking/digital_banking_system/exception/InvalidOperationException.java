package com.digitalbanking.digital_banking_system.exception;

import org.springframework.http.HttpStatus;

public class InvalidOperationException extends BankingException {

    public InvalidOperationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_OPERATION");
    }

    public InvalidOperationException(String operation, String reason) {
        super(String.format("Cannot perform %s: %s", operation, reason),
                HttpStatus.BAD_REQUEST,
                "INVALID_OPERATION");
    }
}
