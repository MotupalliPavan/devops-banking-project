package com.digitalbanking.digital_banking_system.controller;

import com.digitalbanking.digital_banking_system.dto.response.AccountDTO;
import com.digitalbanking.digital_banking_system.dto.response.ApiResponse;
import com.digitalbanking.digital_banking_system.dto.response.UserDTO;
import com.digitalbanking.digital_banking_system.entity.AuditLog;
import com.digitalbanking.digital_banking_system.service.AccountService;
import com.digitalbanking.digital_banking_system.service.AuditService;
import com.digitalbanking.digital_banking_system.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AccountService accountService;
    private final AuditService auditService;

    public AdminController(UserService userService,
                           AccountService accountService,
                           AuditService auditService) {
        this.userService = userService;
        this.accountService = accountService;
        this.auditService = auditService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }

    @PostMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully"));
    }

    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getUserAccounts(@PathVariable Long userId) {
        List<AccountDTO> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @PostMapping("/accounts/{accountNumber}/freeze")
    public ResponseEntity<ApiResponse<Void>> freezeAccount(@PathVariable String accountNumber) {
        accountService.freezeAccount(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account frozen successfully"));
    }

    @PostMapping("/accounts/{accountNumber}/unfreeze")
    public ResponseEntity<ApiResponse<Void>> unfreezeAccount(@PathVariable String accountNumber) {
        accountService.unfreezeAccount(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account unfrozen successfully"));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AuditLog> auditLogs = auditService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }

    @GetMapping("/audit-logs/user/{userId}")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AuditLog> auditLogs = auditService.getAuditLogsByUserId(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }
}
