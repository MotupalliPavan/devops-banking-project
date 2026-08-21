package com.digitalbanking.digital_banking_system.dto.response;

import com.digitalbanking.digital_banking_system.enums.TransactionStatus;
import com.digitalbanking.digital_banking_system.enums.TransactionType;
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
public class TransactionDTO {

    private Long id;
    private String referenceId;
    private TransactionType type;
    private BigDecimal amount;
    private String fromAccountNumber;
    private String toAccountNumber;
    private TransactionStatus status;
    private String description;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
}
