package com.digitalbanking.digital_banking_system.exception;

import org.springframework.http.HttpStatus;

public class AccountNotActiveException extends BankingException {

    public AccountNotActiveException(String accountNumber) {
        super(String.format("Account %s is not active", accountNumber),
                HttpStatus.BAD_REQUEST,
                "ACCOUNT_NOT_ACTIVE");
    }

    public AccountNotActiveException(String accountNumber, String status) {
        super(String.format("Account %s is %s and cannot be used for transactions", accountNumber, status),
                HttpStatus.BAD_REQUEST,
                "ACCOUNT_NOT_ACTIVE");
    }
}
