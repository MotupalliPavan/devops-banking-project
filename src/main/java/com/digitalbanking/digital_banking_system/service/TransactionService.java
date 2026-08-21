package com.digitalbanking.digital_banking_system.service;

import com.digitalbanking.digital_banking_system.dto.request.DepositRequest;
import com.digitalbanking.digital_banking_system.dto.request.TransferRequest;
import com.digitalbanking.digital_banking_system.dto.request.WithdrawRequest;
import com.digitalbanking.digital_banking_system.dto.response.TransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    TransactionDTO deposit(DepositRequest request);

    TransactionDTO withdraw(WithdrawRequest request);

    TransactionDTO transfer(TransferRequest request);

    TransactionDTO getTransactionByReferenceId(String referenceId);

    List<TransactionDTO> getTransactionsByAccountNumber(String accountNumber);

    Page<TransactionDTO> getTransactionsByAccountNumber(String accountNumber, Pageable pageable);

    List<TransactionDTO> getCurrentUserTransactions();

    Page<TransactionDTO> getCurrentUserTransactions(Pageable pageable);
}
