package com.digitalbanking.digital_banking_system.service;

import com.digitalbanking.digital_banking_system.dto.request.CreateAccountRequest;
import com.digitalbanking.digital_banking_system.dto.response.AccountDTO;
import com.digitalbanking.digital_banking_system.dto.response.BalanceDTO;
import com.digitalbanking.digital_banking_system.entity.Account;
import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.AccountStatus;
import com.digitalbanking.digital_banking_system.enums.AccountType;
import com.digitalbanking.digital_banking_system.enums.Role;
import com.digitalbanking.digital_banking_system.enums.Status;
import com.digitalbanking.digital_banking_system.exception.InvalidOperationException;
import com.digitalbanking.digital_banking_system.exception.ResourceNotFoundException;
import com.digitalbanking.digital_banking_system.mapper.AccountMapper;
import com.digitalbanking.digital_banking_system.mapper.TransactionMapper;
import com.digitalbanking.digital_banking_system.repository.AccountRepository;
import com.digitalbanking.digital_banking_system.repository.TransactionRepository;
import com.digitalbanking.digital_banking_system.service.impl.AccountServiceImpl;
import com.digitalbanking.digital_banking_system.util.AccountNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountNumberGenerator accountNumberGenerator;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User testUser;
    private Account testAccount;
    private AccountDTO testAccountDTO;

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

        testAccountDTO = AccountDTO.builder()
                .id(1L)
                .accountNumber("1001123456789012")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .ownerName("John Doe")
                .ownerId(1L)
                .build();
    }

    @Test
    @DisplayName("Should create account successfully")
    void createAccount_Success() {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .accountType(AccountType.SAVINGS)
                .initialDeposit(BigDecimal.valueOf(500))
                .build();

        when(userService.getCurrentUserEntity()).thenReturn(testUser);
        when(accountNumberGenerator.generate()).thenReturn("1001123456789012");
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(testAccountDTO);

        AccountDTO result = accountService.createAccount(request);

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("1001123456789012");
        verify(accountRepository).save(any(Account.class));
        verify(auditService).log(anyString(), anyString(), anyLong(), any(), anyString());
    }

    @Test
    @DisplayName("Should get account by number successfully")
    void getAccountByNumber_Success() {
        when(accountRepository.findByAccountNumber("1001123456789012")).thenReturn(Optional.of(testAccount));
        when(userService.getCurrentUserEntity()).thenReturn(testUser);
        when(accountMapper.toDTO(testAccount)).thenReturn(testAccountDTO);

        AccountDTO result = accountService.getAccountByNumber("1001123456789012");

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("1001123456789012");
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void getAccountByNumber_NotFound() {
        when(accountRepository.findByAccountNumber("9999999999999999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountByNumber("9999999999999999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account");
    }

    @Test
    @DisplayName("Should get balance successfully")
    void getBalance_Success() {
        when(accountRepository.findByAccountNumber("1001123456789012")).thenReturn(Optional.of(testAccount));
        when(userService.getCurrentUserEntity()).thenReturn(testUser);

        BalanceDTO result = accountService.getBalance("1001123456789012");

        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("Should get current user accounts")
    void getCurrentUserAccounts_Success() {
        List<Account> accounts = Arrays.asList(testAccount);
        List<AccountDTO> accountDTOs = Arrays.asList(testAccountDTO);

        when(userService.getCurrentUserEntity()).thenReturn(testUser);
        when(accountRepository.findByUser(testUser)).thenReturn(accounts);
        when(accountMapper.toDTOList(accounts)).thenReturn(accountDTOs);

        List<AccountDTO> result = accountService.getCurrentUserAccounts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountNumber()).isEqualTo("1001123456789012");
    }

    @Test
    @DisplayName("Should freeze account successfully")
    void freezeAccount_Success() {
        when(accountRepository.findByAccountNumber("1001123456789012")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        accountService.freezeAccount("1001123456789012");

        assertThat(testAccount.getStatus()).isEqualTo(AccountStatus.FROZEN);
        verify(auditService).log(anyString(), anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when freezing already frozen account")
    void freezeAccount_AlreadyFrozen() {
        testAccount.setStatus(AccountStatus.FROZEN);
        when(accountRepository.findByAccountNumber("1001123456789012")).thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> accountService.freezeAccount("1001123456789012"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("already frozen");
    }

    @Test
    @DisplayName("Should close account with zero balance")
    void closeAccount_Success() {
        testAccount.setBalance(BigDecimal.ZERO);
        when(accountRepository.findByAccountNumber("1001123456789012")).thenReturn(Optional.of(testAccount));
        when(userService.getCurrentUserEntity()).thenReturn(testUser);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        accountService.closeAccount("1001123456789012");

        assertThat(testAccount.getStatus()).isEqualTo(AccountStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should throw exception when closing account with balance")
    void closeAccount_HasBalance() {
        when(accountRepository.findByAccountNumber("1001123456789012")).thenReturn(Optional.of(testAccount));
        when(userService.getCurrentUserEntity()).thenReturn(testUser);

        assertThatThrownBy(() -> accountService.closeAccount("1001123456789012"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("balance must be zero");
    }
}
