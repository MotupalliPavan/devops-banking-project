package com.digitalbanking.digital_banking_system.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private static final String BANK_CODE = "1001";
    private static final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder accountNumber = new StringBuilder(BANK_CODE);

        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }
}
