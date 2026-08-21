package com.digitalbanking.digital_banking_system.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class InsufficientBalanceException extends BankingException {

    public InsufficientBalanceException(String accountNumber, BigDecimal requested, BigDecimal available) {
        super(String.format("Insufficient balance in account %s. Requested: %s, Available: %s",
                        accountNumber, requested, available),
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_BALANCE");
    }

    public InsufficientBalanceException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INSUFFICIENT_BALANCE");
    }
}
