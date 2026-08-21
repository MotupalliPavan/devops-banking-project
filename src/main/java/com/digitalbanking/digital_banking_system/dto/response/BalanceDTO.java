package com.digitalbanking.digital_banking_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDTO {

    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime asOf;

    public static BalanceDTO of(String accountNumber, BigDecimal balance) {
        return BalanceDTO.builder()
                .accountNumber(accountNumber)
                .balance(balance)
                .currency("USD")
                .asOf(LocalDateTime.now())
                .build();
    }
}
