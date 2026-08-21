package com.digitalbanking.digital_banking_system.service.impl;

import com.digitalbanking.digital_banking_system.dto.request.CreateAccountRequest;
import com.digitalbanking.digital_banking_system.dto.response.AccountDTO;
import com.digitalbanking.digital_banking_system.dto.response.AccountStatementDTO;
import com.digitalbanking.digital_banking_system.dto.response.BalanceDTO;
import com.digitalbanking.digital_banking_system.dto.response.TransactionDTO;
import com.digitalbanking.digital_banking_system.entity.Account;
import com.digitalbanking.digital_banking_system.entity.Transaction;
import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.AccountStatus;
import com.digitalbanking.digital_banking_system.exception.AccountNotActiveException;
import com.digitalbanking.digital_banking_system.exception.InvalidOperationException;
import com.digitalbanking.digital_banking_system.exception.ResourceNotFoundException;
import com.digitalbanking.digital_banking_system.mapper.AccountMapper;
import com.digitalbanking.digital_banking_system.mapper.TransactionMapper;
import com.digitalbanking.digital_banking_system.repository.AccountRepository;
import com.digitalbanking.digital_banking_system.repository.TransactionRepository;
import com.digitalbanking.digital_banking_system.service.AccountService;
import com.digitalbanking.digital_banking_system.service.AuditService;
import com.digitalbanking.digital_banking_system.service.UserService;
import com.digitalbanking.digital_banking_system.util.AccountNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AuditService auditService;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              UserService userService,
                              AccountMapper accountMapper,
                              TransactionMapper transactionMapper,
                              AccountNumberGenerator accountNumberGenerator,
                              AuditService auditService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
        this.accountNumberGenerator = accountNumberGenerator;
        this.auditService = auditService;
    }

    @Override
    public AccountDTO createAccount(CreateAccountRequest request) {
        User currentUser = userService.getCurrentUserEntity();

        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        BigDecimal initialDeposit = request.getInitialDeposit() != null ?
                request.getInitialDeposit() : BigDecimal.ZERO;

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .user(currentUser)
                .balance(initialDeposit)
                .status(AccountStatus.ACTIVE)
                .build();

        Account savedAccount = accountRepository.save(account);

        auditService.log("CREATE_ACCOUNT", "Account", savedAccount.getId(),
                null, "accountNumber=" + accountNumber);

        return accountMapper.toDTO(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        validateAccountOwnership(account);
        return accountMapper.toDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
        validateAccountOwnership(account);
        return accountMapper.toDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountEntityByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getCurrentUserAccounts() {
        User currentUser = userService.getCurrentUserEntity();
        List<Account> accounts = accountRepository.findByUser(currentUser);
        return accountMapper.toDTOList(accounts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByUserId(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accountMapper.toDTOList(accounts);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceDTO getBalance(String accountNumber) {
        Account account = getAccountEntityByNumber(accountNumber);
        validateAccountOwnership(account);
        return BalanceDTO.of(accountNumber, account.getBalance());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountStatementDTO getAccountStatement(String accountNumber,
                                                    LocalDateTime fromDate,
                                                    LocalDateTime toDate) {
        Account account = getAccountEntityByNumber(accountNumber);
        validateAccountOwnership(account);

        List<Transaction> transactions = transactionRepository
                .findByAccountAndDateRange(account, fromDate, toDate);

        List<TransactionDTO> transactionDTOs = transactionMapper.toDTOList(transactions);

        BigDecimal totalCredits = transactions.stream()
                .filter(t -> t.getToAccount() != null && t.getToAccount().equals(account))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebits = transactions.stream()
                .filter(t -> t.getFromAccount() != null && t.getFromAccount().equals(account))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AccountStatementDTO.builder()
                .account(accountMapper.toDTO(account))
                .fromDate(fromDate)
                .toDate(toDate)
                .closingBalance(account.getBalance())
                .totalCredits(totalCredits)
                .totalDebits(totalDebits)
                .transactionCount(transactions.size())
                .transactions(transactionDTOs)
                .build();
    }

    @Override
    public void freezeAccount(String accountNumber) {
        Account account = getAccountEntityByNumber(accountNumber);

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidOperationException("freeze account", "Account is already frozen");
        }

        String oldStatus = account.getStatus().name();
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);

        auditService.log("FREEZE_ACCOUNT", "Account", account.getId(), oldStatus, AccountStatus.FROZEN.name());
    }

    @Override
    public void unfreezeAccount(String accountNumber) {
        Account account = getAccountEntityByNumber(accountNumber);

        if (account.getStatus() != AccountStatus.FROZEN) {
            throw new InvalidOperationException("unfreeze account", "Account is not frozen");
        }

        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);

        auditService.log("UNFREEZE_ACCOUNT", "Account", account.getId(),
                AccountStatus.FROZEN.name(), AccountStatus.ACTIVE.name());
    }

    @Override
    public void closeAccount(String accountNumber) {
        Account account = getAccountEntityByNumber(accountNumber);
        validateAccountOwnership(account);

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidOperationException("close account", "Account balance must be zero");
        }

        String oldStatus = account.getStatus().name();
        account.setStatus(AccountStatus.INACTIVE);
        accountRepository.save(account);

        auditService.log("CLOSE_ACCOUNT", "Account", account.getId(), oldStatus, AccountStatus.INACTIVE.name());
    }

    public void validateAccountActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(account.getAccountNumber(), account.getStatus().name());
        }
    }

    private void validateAccountOwnership(Account account) {
        User currentUser = userService.getCurrentUserEntity();
        if (!account.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().name().equals("ADMIN")) {
            throw new InvalidOperationException("access account", "You don't have access to this account");
        }
    }
}
