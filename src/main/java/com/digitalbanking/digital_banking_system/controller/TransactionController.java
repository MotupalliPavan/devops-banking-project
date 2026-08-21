package com.digitalbanking.digital_banking_system.controller;

import com.digitalbanking.digital_banking_system.dto.request.DepositRequest;
import com.digitalbanking.digital_banking_system.dto.request.TransferRequest;
import com.digitalbanking.digital_banking_system.dto.request.WithdrawRequest;
import com.digitalbanking.digital_banking_system.dto.response.ApiResponse;
import com.digitalbanking.digital_banking_system.dto.response.TransactionDTO;
import com.digitalbanking.digital_banking_system.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionDTO>> deposit(@Valid @RequestBody DepositRequest request) {
        TransactionDTO transaction = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit successful", transaction));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionDTO>> withdraw(@Valid @RequestBody WithdrawRequest request) {
        TransactionDTO transaction = transactionService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal successful", transaction));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionDTO>> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionDTO transaction = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer successful", transaction));
    }

    @GetMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionByReferenceId(@PathVariable String referenceId) {
        TransactionDTO transaction = transactionService.getTransactionByReferenceId(referenceId);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getMyTransactions() {
        List<TransactionDTO> transactions = transactionService.getCurrentUserTransactions();
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getMyTransactionsPaginated(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<TransactionDTO> transactions = transactionService.getCurrentUserTransactions(pageable);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByAccount(
            @PathVariable String accountNumber) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/account/{accountNumber}/paginated")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getTransactionsByAccountPaginated(
            @PathVariable String accountNumber,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<TransactionDTO> transactions = transactionService.getTransactionsByAccountNumber(accountNumber, pageable);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}
