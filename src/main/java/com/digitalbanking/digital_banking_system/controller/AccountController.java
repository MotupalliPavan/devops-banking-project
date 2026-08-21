package com.digitalbanking.digital_banking_system.controller;

import com.digitalbanking.digital_banking_system.dto.request.CreateAccountRequest;
import com.digitalbanking.digital_banking_system.dto.response.AccountDTO;
import com.digitalbanking.digital_banking_system.dto.response.AccountStatementDTO;
import com.digitalbanking.digital_banking_system.dto.response.ApiResponse;
import com.digitalbanking.digital_banking_system.dto.response.BalanceDTO;
import com.digitalbanking.digital_banking_system.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountDTO account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", account));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getMyAccounts() {
        List<AccountDTO> accounts = accountService.getCurrentUserAccounts();
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountByNumber(@PathVariable String accountNumber) {
        AccountDTO account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<BalanceDTO>> getBalance(@PathVariable String accountNumber) {
        BalanceDTO balance = accountService.getBalance(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(balance));
    }

    @GetMapping("/{accountNumber}/statement")
    public ResponseEntity<ApiResponse<AccountStatementDTO>> getAccountStatement(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        AccountStatementDTO statement = accountService.getAccountStatement(accountNumber, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(statement));
    }

    @PostMapping("/{accountNumber}/close")
    public ResponseEntity<ApiResponse<Void>> closeAccount(@PathVariable String accountNumber) {
        accountService.closeAccount(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account closed successfully"));
    }
}
