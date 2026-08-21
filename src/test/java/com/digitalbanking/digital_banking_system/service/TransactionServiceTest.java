package com.digitalbanking.digital_banking_system.service;

import com.digitalbanking.digital_banking_system.dto.request.DepositRequest;
import com.digitalbanking.digital_banking_system.dto.request.TransferRequest;
import com.digitalbanking.digital_banking_system.dto.request.WithdrawRequest;
import com.digitalbanking.digital_banking_system.dto.response.TransactionDTO;
import com.digitalbanking.digital_banking_system.entity.Account;
import com.digitalbanking.digital_banking_system.entity.Transaction;
import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.*;
import com.digitalbanking.digital_banking_system.exception.AccountNotActiveException;
import com.digitalbanking.digital_banking_system.exception.InsufficientBalanceException;
import com.digitalbanking.digital_banking_system.exception.InvalidOperationException;
import com.digitalbanking.digital_banking_system.mapper.TransactionMapper;
import com.digitalbanking.digital_banking_system.repository.AccountRepository;
import com.digitalbanking.digital_banking_system.repository.TransactionRepository;
import com.digitalbanking.digital_banking_system.service.impl.TransactionServiceImpl;
import com.digitalbanking.digital_banking_system.util.TransactionReferenceGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransactionReferenceGenerator referenceGenerator;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private Account testAccount;
    private Account targetAccount;
    private Transaction testTransaction;
    private TransactionDTO testTransactionDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .build();

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("1001123456789012")
                .accountType(AccountType.SAVINGS)
                .user(testUser)
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .build();

        targetAccount = Account.builder()
                .id(2L)
                .accountNumber("1001987654321098")
                .accountType(AccountType.SAVINGS)
                .user(testUser)
                .balance(BigDecimal.valueOf(500))
                .status(AccountStatus.ACTIVE)
                .build();

        testTransaction = Transaction.builder()
                .id(1L)
                .referenceId("TXN20250101123456ABC")
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .toAccount(testAccount)
                .status(TransactionStatus.COMPLETED)
                .balanceAfter(BigDecimal.valueOf(1100))
                .build();

        testTransactionDTO = TransactionDTO.builder()
                .id(1L)
                .referenceId("TXN20250101123456ABC")
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .toAccountNumber("1001123456789012")
                .status(TransactionStatus.COMPLETED)
                .balanceAfter(BigDecimal.valueOf(1100))
                .build();
    }

    @Test
    @DisplayName("Should deposit successfully")
    void deposit_Success() {
        DepositRequest request = DepositRequest.builder()
                .accountNumber("1001123456789012")
                .amount(BigDecimal.valueOf(100))
                .description("Test deposit")
                .build();

        when(accountRepository.findByAccountNumberWithLock("1001123456789012"))
                .thenReturn(Optional.of(testAccount));
        when(userService.getCurrentUserEntity()).thenReturn(testUser);
        when(referenceGenerator.generate()).thenReturn("TXN20250101123456ABC");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        TransactionDTO result = transactionService.deposit(request);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(testAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1100));
        verify(accountRepository).save(testAccount);
    }

    @Test
    @DisplayName("Should withdraw successfully")
    void withdraw_Success() {
        WithdrawRequest request = WithdrawRequest.builder()
                .accountNumber("1001123456789012")
                .amount(BigDecimal.valueOf(100))
                .description("Test withdrawal")
                .build();

        testTransaction.setType(TransactionType.WITHDRAWAL);
        testTransactionDTO.setType(TransactionType.WITHDRAWAL);

        when(accountRepository.findByAccountNumberWithLock("1001123456789012"))
                .thenReturn(Optional.of(testAccount));
        when(userService.getCurrentUserEntity()).thenReturn(testUser);
        when(referenceGenerator.generate()).thenReturn("TXN20250101123456ABC");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        TransactionDTO result = transactionService.withdraw(request);

        assertThat(result).isNotNull();
        assertThat(testAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(900));
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance for withdrawal")
    void withdraw_InsufficientBalance() {
        WithdrawRequest request = WithdrawRequest.builder()
                .accountNumber("1001123456789012")
                .amount(BigDecimal.valueOf(2000))
                .build();

        when(accountRepository.findByAccountNumberWithLock("1001123456789012"))
                .thenReturn(Optional.of(testAccount));
        when(userService.getCurrentUserEntity()).thenReturn(testUser);

        assertThatThrownBy(() -> transactionService.withdraw(request))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    @DisplayName("Should transfer successfully")
    void transfer_Success() {
        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("1001123456789012")
                .toAccountNumber("1001987654321098")
                .amount(BigDecimal.valueOf(100))
                .description("Test transfer")
                .build();

        testTransaction.setType(TransactionType.TRANSFER);
        testTransactionDTO.setType(TransactionType.TRANSFER);

        when(accountRepository.findByAccountNumberWithLock("1001123456789012"))
                .thenReturn(Optional.of(testAccount));
        when(accountRepository.findByAccountNumberWithLock("1001987654321098"))
                .thenReturn(Optional.of(targetAccount));
        when(userService.getCurrentUserEntity()).thenReturn(testUser);
        when(referenceGenerator.generate()).thenReturn("TXN20250101123456ABC");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        TransactionDTO result = transactionService.transfer(request);

        assertThat(result).isNotNull();
        assertThat(testAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(900));
        assertThat(targetAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(600));
    }

    @Test
    @DisplayName("Should throw exception when transferring to same account")
    void transfer_SameAccount() {
        TransferRequest request = TransferRequest.builder()
                .fromAccountNumber("1001123456789012")
                .toAccountNumber("1001123456789012")
                .amount(BigDecimal.valueOf(100))
                .build();

        assertThatThrownBy(() -> transactionService.transfer(request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("cannot be the same");
    }

    @Test
    @DisplayName("Should throw exception when account is frozen")
    void deposit_FrozenAccount() {
        testAccount.setStatus(AccountStatus.FROZEN);
        DepositRequest request = DepositRequest.builder()
                .accountNumber("1001123456789012")
                .amount(BigDecimal.valueOf(100))
                .build();

        when(accountRepository.findByAccountNumberWithLock("1001123456789012"))
                .thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> transactionService.deposit(request))
                .isInstanceOf(AccountNotActiveException.class);
    }
}
