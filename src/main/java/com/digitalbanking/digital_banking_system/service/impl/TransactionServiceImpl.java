package com.digitalbanking.digital_banking_system.service.impl;

import com.digitalbanking.digital_banking_system.dto.request.DepositRequest;
import com.digitalbanking.digital_banking_system.dto.request.TransferRequest;
import com.digitalbanking.digital_banking_system.dto.request.WithdrawRequest;
import com.digitalbanking.digital_banking_system.dto.response.TransactionDTO;
import com.digitalbanking.digital_banking_system.entity.Account;
import com.digitalbanking.digital_banking_system.entity.Transaction;
import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.AccountStatus;
import com.digitalbanking.digital_banking_system.enums.TransactionStatus;
import com.digitalbanking.digital_banking_system.enums.TransactionType;
import com.digitalbanking.digital_banking_system.exception.AccountNotActiveException;
import com.digitalbanking.digital_banking_system.exception.InsufficientBalanceException;
import com.digitalbanking.digital_banking_system.exception.InvalidOperationException;
import com.digitalbanking.digital_banking_system.exception.ResourceNotFoundException;
import com.digitalbanking.digital_banking_system.mapper.TransactionMapper;
import com.digitalbanking.digital_banking_system.repository.AccountRepository;
import com.digitalbanking.digital_banking_system.repository.TransactionRepository;
import com.digitalbanking.digital_banking_system.service.AuditService;
import com.digitalbanking.digital_banking_system.service.TransactionService;
import com.digitalbanking.digital_banking_system.service.UserService;
import com.digitalbanking.digital_banking_system.util.TransactionReferenceGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final TransactionMapper transactionMapper;
    private final TransactionReferenceGenerator referenceGenerator;
    private final AuditService auditService;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountRepository accountRepository,
                                  UserService userService,
                                  TransactionMapper transactionMapper,
                                  TransactionReferenceGenerator referenceGenerator,
                                  AuditService auditService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.transactionMapper = transactionMapper;
        this.referenceGenerator = referenceGenerator;
        this.auditService = auditService;
    }

    @Override
    public TransactionDTO deposit(DepositRequest request) {
        Account account = accountRepository.findByAccountNumberWithLock(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getAccountNumber()));

        validateAccountActive(account);
        validateAccountOwnership(account);

        account.credit(request.getAmount());

        Transaction transaction = Transaction.builder()
                .referenceId(referenceGenerator.generate())
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .toAccount(account)
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription() != null ? request.getDescription() : "Cash deposit")
                .balanceAfter(account.getBalance())
                .build();

        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        auditService.log("DEPOSIT", "Transaction", savedTransaction.getId(),
                null, "amount=" + request.getAmount() + ",account=" + request.getAccountNumber());

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public TransactionDTO withdraw(WithdrawRequest request) {
        Account account = accountRepository.findByAccountNumberWithLock(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getAccountNumber()));

        validateAccountActive(account);
        validateAccountOwnership(account);

        if (!account.hasSufficientBalance(request.getAmount())) {
            throw new InsufficientBalanceException(
                    request.getAccountNumber(),
                    request.getAmount(),
                    account.getBalance()
            );
        }

        account.debit(request.getAmount());

        Transaction transaction = Transaction.builder()
                .referenceId(referenceGenerator.generate())
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .fromAccount(account)
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription() != null ? request.getDescription() : "Cash withdrawal")
                .balanceAfter(account.getBalance())
                .build();

        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);

        auditService.log("WITHDRAWAL", "Transaction", savedTransaction.getId(),
                null, "amount=" + request.getAmount() + ",account=" + request.getAccountNumber());

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public TransactionDTO transfer(TransferRequest request) {
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new InvalidOperationException("transfer", "Source and destination accounts cannot be the same");
        }

        Account fromAccount = accountRepository.findByAccountNumberWithLock(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getFromAccountNumber()));

        Account toAccount = accountRepository.findByAccountNumberWithLock(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getToAccountNumber()));

        validateAccountActive(fromAccount);
        validateAccountActive(toAccount);
        validateAccountOwnership(fromAccount);

        if (!fromAccount.hasSufficientBalance(request.getAmount())) {
            throw new InsufficientBalanceException(
                    request.getFromAccountNumber(),
                    request.getAmount(),
                    fromAccount.getBalance()
            );
        }

        fromAccount.debit(request.getAmount());
        toAccount.credit(request.getAmount());

        Transaction transaction = Transaction.builder()
                .referenceId(referenceGenerator.generate())
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription() != null ? request.getDescription() : "Fund transfer")
                .balanceAfter(fromAccount.getBalance())
                .build();

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        Transaction savedTransaction = transactionRepository.save(transaction);

        auditService.log("TRANSFER", "Transaction", savedTransaction.getId(),
                null, "amount=" + request.getAmount() +
                        ",from=" + request.getFromAccountNumber() +
                        ",to=" + request.getToAccountNumber());

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionByReferenceId(String referenceId) {
        Transaction transaction = transactionRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "referenceId", referenceId));
        return transactionMapper.toDTO(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
        validateAccountOwnership(account);

        List<Transaction> transactions = transactionRepository.findByAccount(account);
        return transactionMapper.toDTOList(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getTransactionsByAccountNumber(String accountNumber, Pageable pageable) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));
        validateAccountOwnership(account);

        return transactionRepository.findByAccount(account, pageable)
                .map(transactionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getCurrentUserTransactions() {
        User currentUser = userService.getCurrentUserEntity();
        List<Transaction> transactions = transactionRepository.findByUserId(currentUser.getId());
        return transactionMapper.toDTOList(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getCurrentUserTransactions(Pageable pageable) {
        User currentUser = userService.getCurrentUserEntity();
        return transactionRepository.findByUserId(currentUser.getId(), pageable)
                .map(transactionMapper::toDTO);
    }

    private void validateAccountActive(Account account) {
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
