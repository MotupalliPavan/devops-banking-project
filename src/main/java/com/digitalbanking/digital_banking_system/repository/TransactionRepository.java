package com.digitalbanking.digital_banking_system.repository;

import com.digitalbanking.digital_banking_system.entity.Account;
import com.digitalbanking.digital_banking_system.entity.Transaction;
import com.digitalbanking.digital_banking_system.enums.TransactionStatus;
import com.digitalbanking.digital_banking_system.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByReferenceId(String referenceId);

    List<Transaction> findByFromAccount(Account account);

    List<Transaction> findByToAccount(Account account);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :account OR t.toAccount = :account ORDER BY t.createdAt DESC")
    List<Transaction> findByAccount(@Param("account") Account account);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :account OR t.toAccount = :account ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccount(@Param("account") Account account, Pageable pageable);

    @Query("SELECT t FROM Transaction t LEFT JOIN t.fromAccount fa LEFT JOIN t.toAccount ta WHERE (fa.id = :accountId OR ta.id = :accountId) ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT t FROM Transaction t LEFT JOIN t.fromAccount fa LEFT JOIN t.toAccount ta WHERE (fa.id = :accountId OR ta.id = :accountId) ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByStatus(TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount = :account OR t.toAccount = :account) AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountAndDateRange(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Transaction t LEFT JOIN t.fromAccount fa LEFT JOIN t.toAccount ta WHERE (fa.user.id = :userId OR ta.user.id = :userId) ORDER BY t.createdAt DESC")
    List<Transaction> findByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t LEFT JOIN t.fromAccount fa LEFT JOIN t.toAccount ta WHERE (fa.user.id = :userId OR ta.user.id = :userId) ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transaction t LEFT JOIN t.fromAccount fa LEFT JOIN t.toAccount ta WHERE (fa.id = :accountId OR ta.id = :accountId)")
    long countByAccountId(@Param("accountId") Long accountId);

    boolean existsByReferenceId(String referenceId);
}
