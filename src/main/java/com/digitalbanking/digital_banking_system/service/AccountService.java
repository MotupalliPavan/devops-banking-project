package com.digitalbanking.digital_banking_system.service;

import com.digitalbanking.digital_banking_system.dto.request.CreateAccountRequest;
import com.digitalbanking.digital_banking_system.dto.response.AccountDTO;
import com.digitalbanking.digital_banking_system.dto.response.AccountStatementDTO;
import com.digitalbanking.digital_banking_system.dto.response.BalanceDTO;
import com.digitalbanking.digital_banking_system.entity.Account;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountService {

    AccountDTO createAccount(CreateAccountRequest request);

    AccountDTO getAccountById(Long id);

    AccountDTO getAccountByNumber(String accountNumber);

    Account getAccountEntityByNumber(String accountNumber);

    List<AccountDTO> getCurrentUserAccounts();

    List<AccountDTO> getAccountsByUserId(Long userId);

    BalanceDTO getBalance(String accountNumber);

    AccountStatementDTO getAccountStatement(String accountNumber, LocalDateTime fromDate, LocalDateTime toDate);

    void freezeAccount(String accountNumber);

    void unfreezeAccount(String accountNumber);

    void closeAccount(String accountNumber);
}
