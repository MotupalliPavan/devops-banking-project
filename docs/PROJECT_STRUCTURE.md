# Digital Banking System - Project Structure Guide

## Overview

This document provides a comprehensive guide to the project structure, explaining the purpose of each directory and file. This serves as a blueprint for building the application from scratch.

---

## Complete Project Structure

```
digital-banking-system/
├── .github/
│   └── workflows/
│       └── ci.yml                    # GitHub Actions CI/CD pipeline
├── docs/
│   ├── DESIGN.md                     # System design document
│   ├── API.md                        # API documentation
│   ├── FEATURES.md                   # Feature specifications
│   ├── TECH_STACK.md                 # Technology stack details
│   └── PROJECT_STRUCTURE.md          # This file
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── banking/
│   │   │           ├── BankingApplication.java
│   │   │           ├── config/
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   ├── JwtConfig.java
│   │   │           │   ├── OpenApiConfig.java
│   │   │           │   └── WebConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── UserController.java
│   │   │           │   ├── AccountController.java
│   │   │           │   ├── TransactionController.java
│   │   │           │   ├── AdminController.java
│   │   │           │   └── web/
│   │   │           │       ├── DashboardController.java
│   │   │           │       ├── AccountWebController.java
│   │   │           │       └── TransactionWebController.java
│   │   │           ├── dto/
│   │   │           │   ├── request/
│   │   │           │   │   ├── CreateUserRequest.java
│   │   │           │   │   ├── LoginRequest.java
│   │   │           │   │   ├── CreateAccountRequest.java
│   │   │           │   │   ├── DepositRequest.java
│   │   │           │   │   ├── WithdrawRequest.java
│   │   │           │   │   ├── TransferRequest.java
│   │   │           │   │   └── ChangePasswordRequest.java
│   │   │           │   ├── response/
│   │   │           │   │   ├── ApiResponse.java
│   │   │           │   │   ├── AuthResponse.java
│   │   │           │   │   ├── UserDTO.java
│   │   │           │   │   ├── AccountDTO.java
│   │   │           │   │   ├── TransactionDTO.java
│   │   │           │   │   ├── AccountStatementDTO.java
│   │   │           │   │   └── DashboardStatsDTO.java
│   │   │           │   └── mapper/
│   │   │           │       ├── UserMapper.java
│   │   │           │       ├── AccountMapper.java
│   │   │           │       └── TransactionMapper.java
│   │   │           ├── entity/
│   │   │           │   ├── User.java
│   │   │           │   ├── Account.java
│   │   │           │   ├── Transaction.java
│   │   │           │   ├── AuditLog.java
│   │   │           │   └── enums/
│   │   │           │       ├── Role.java
│   │   │           │       ├── Status.java
│   │   │           │       ├── AccountType.java
│   │   │           │       ├── AccountStatus.java
│   │   │           │       └── TransactionType.java
│   │   │           ├── repository/
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── AccountRepository.java
│   │   │           │   ├── TransactionRepository.java
│   │   │           │   └── AuditLogRepository.java
│   │   │           ├── service/
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── UserService.java
│   │   │           │   ├── AccountService.java
│   │   │           │   ├── TransactionService.java
│   │   │           │   ├── JwtService.java
│   │   │           │   ├── AuditService.java
│   │   │           │   └── impl/
│   │   │           │       ├── AuthServiceImpl.java
│   │   │           │       ├── UserServiceImpl.java
│   │   │           │       ├── AccountServiceImpl.java
│   │   │           │       ├── TransactionServiceImpl.java
│   │   │           │       └── AuditServiceImpl.java
│   │   │           ├── security/
│   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │           │   ├── JwtAuthenticationEntryPoint.java
│   │   │           │   └── CustomUserDetailsService.java
│   │   │           ├── exception/
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   ├── BankingException.java
│   │   │           │   ├── ResourceNotFoundException.java
│   │   │           │   ├── InsufficientBalanceException.java
│   │   │           │   ├── AccountNotActiveException.java
│   │   │           │   ├── DuplicateResourceException.java
│   │   │           │   └── InvalidOperationException.java
│   │   │           └── util/
│   │   │               ├── AccountNumberGenerator.java
│   │   │               ├── TransactionReferenceGenerator.java
│   │   │               └── DateUtils.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-docker.yml
│   │       ├── application-prod.yml
│   │       ├── logback-spring.xml
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1__create_users_table.sql
│   │       │       ├── V2__create_accounts_table.sql
│   │       │       ├── V3__create_transactions_table.sql
│   │       │       ├── V4__create_audit_logs_table.sql
│   │       │       └── V5__add_indexes.sql
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── styles.css
│   │       │   └── js/
│   │       │       └── app.js
│   │       └── templates/
│   │           ├── layout/
│   │           │   ├── header.html
│   │           │   ├── footer.html
│   │           │   └── sidebar.html
│   │           ├── fragments/
│   │           │   ├── alerts.html
│   │           │   └── pagination.html
│   │           ├── auth/
│   │           │   ├── login.html
│   │           │   └── register.html
│   │           ├── dashboard.html
│   │           ├── accounts/
│   │           │   ├── list.html
│   │           │   ├── details.html
│   │           │   └── create.html
│   │           ├── transactions/
│   │           │   ├── history.html
│   │           │   ├── transfer.html
│   │           │   ├── deposit.html
│   │           │   └── withdraw.html
│   │           ├── profile/
│   │           │   ├── view.html
│   │           │   └── edit.html
│   │           ├── admin/
│   │           │   ├── dashboard.html
│   │           │   ├── users.html
│   │           │   └── accounts.html
│   │           └── error/
│   │               ├── 404.html
│   │               ├── 403.html
│   │               └── 500.html
│   └── test/
│       └── java/
│           └── com/
│               └── banking/
│                   ├── BankingApplicationTests.java
│                   ├── controller/
│                   │   ├── AuthControllerTest.java
│                   │   ├── AccountControllerTest.java
│                   │   └── TransactionControllerTest.java
│                   ├── service/
│                   │   ├── AuthServiceTest.java
│                   │   ├── AccountServiceTest.java
│                   │   └── TransactionServiceTest.java
│                   ├── repository/
│                   │   ├── UserRepositoryTest.java
│                   │   ├── AccountRepositoryTest.java
│                   │   └── TransactionRepositoryTest.java
│                   └── integration/
│                       ├── AuthIntegrationTest.java
│                       └── TransactionIntegrationTest.java
├── docker/
│   └── oracle/
│       └── init.sql                  # Database initialization script
├── .env.example                      # Environment variables template
├── .gitignore
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## Directory Details

### 1. Root Level Files

#### `.env.example`
```properties
# Database Configuration
ORACLE_PASSWORD=SecureRootPass123
DB_PASSWORD=BankingUserPass123

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters-long

# Application
SPRING_PROFILES_ACTIVE=dev
```

#### `.gitignore`
```gitignore
# Compiled class files
target/

# IDE files
.idea/
*.iml
.vscode/
*.swp

# Logs
logs/
*.log

# Environment files
.env
*.env.local

# OS files
.DS_Store
Thumbs.db

# Maven
!.mvn/wrapper/maven-wrapper.jar
```

---

### 2. Source Code Structure (`src/main/java`)

#### 2.1 Main Application Class

**`BankingApplication.java`**
```java
package com.banking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }
}
```

---

#### 2.2 Configuration Package (`config/`)

Contains all Spring configuration classes.

| File | Purpose |
|------|---------|
| `SecurityConfig.java` | Spring Security configuration |
| `JwtConfig.java` | JWT properties configuration |
| `OpenApiConfig.java` | Swagger/OpenAPI configuration |
| `WebConfig.java` | Web MVC configuration (CORS, etc.) |

**Example: `SecurityConfig.java`**
```java
package com.banking.config;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

#### 2.3 Controller Package (`controller/`)

REST API controllers for handling HTTP requests.

| File | Endpoints | Purpose |
|------|-----------|---------|
| `AuthController.java` | `/api/v1/auth/*` | Authentication endpoints |
| `UserController.java` | `/api/v1/users/*` | User profile management |
| `AccountController.java` | `/api/v1/accounts/*` | Account operations |
| `TransactionController.java` | `/api/v1/transactions/*` | Transaction operations |
| `AdminController.java` | `/api/v1/admin/*` | Admin operations |

**Web Controllers (`controller/web/`)** - For Thymeleaf views:

| File | Purpose |
|------|---------|
| `DashboardController.java` | Main dashboard view |
| `AccountWebController.java` | Account management views |
| `TransactionWebController.java` | Transaction views |

**Example: `AccountController.java`**
```java
package com.banking.controller;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    @Operation(summary = "Get all accounts for current user")
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getAllAccounts(
            @AuthenticationPrincipal UserDetails user) {
        List<AccountDTO> accounts = accountService.getAccountsByUser(user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by account number")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccount(
            @PathVariable String accountNumber) {
        AccountDTO account = accountService.getAccount(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    @PostMapping
    @Operation(summary = "Create new account")
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal UserDetails user) {
        AccountDTO account = accountService.createAccount(user.getUsername(), request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Account created successfully", account));
    }

    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get account balance")
    public ResponseEntity<ApiResponse<BalanceDTO>> getBalance(
            @PathVariable String accountNumber) {
        BalanceDTO balance = accountService.getBalance(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(balance));
    }
}
```

---

#### 2.4 DTO Package (`dto/`)

Data Transfer Objects for API requests and responses.

**Request DTOs (`dto/request/`)**
```java
// CreateUserRequest.java
public record CreateUserRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*$",
             message = "Password must contain uppercase, number, and special character")
    String password,

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100)
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100)
    String lastName,

    @Pattern(regexp = "^\\+91[0-9]{10}$", message = "Invalid phone number")
    String phone
) {}

// TransferRequest.java
public record TransferRequest(
    @NotBlank(message = "Source account is required")
    String fromAccountNumber,

    @NotBlank(message = "Destination account is required")
    String toAccountNumber,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @DecimalMax(value = "500000", message = "Maximum transfer limit is 5,00,000")
    BigDecimal amount,

    @Size(max = 500)
    String description
) {}
```

**Response DTOs (`dto/response/`)**
```java
// ApiResponse.java - Generic API response wrapper
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    Object error,
    LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, null,
            Map.of("code", code, "message", message), LocalDateTime.now());
    }
}

// AccountDTO.java
public record AccountDTO(
    Long id,
    String accountNumber,
    String accountType,
    BigDecimal balance,
    String status,
    String holderName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

**Mappers (`dto/mapper/`)**
```java
// AccountMapper.java
@Component
public class AccountMapper {

    public AccountDTO toDTO(Account account) {
        return new AccountDTO(
            account.getId(),
            account.getAccountNumber(),
            account.getAccountType().name(),
            account.getBalance(),
            account.getStatus().name(),
            account.getUser().getFirstName() + " " + account.getUser().getLastName(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }

    public List<AccountDTO> toDTOList(List<Account> accounts) {
        return accounts.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}
```

---

#### 2.5 Entity Package (`entity/`)

JPA entities representing database tables.

**`User.java`**
```java
package com.banking.entity;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CUSTOMER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != Status.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == Status.ACTIVE;
    }
}
```

**`Account.java`**
```java
package com.banking.entity;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version; // Optimistic locking
}
```

**`Transaction.java`**
```java
package com.banking.entity;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_id", nullable = false, unique = true)
    private String referenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    private String description;

    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

**Enums (`entity/enums/`)**
```java
// Role.java
public enum Role {
    CUSTOMER, ADMIN
}

// Status.java
public enum Status {
    ACTIVE, INACTIVE, SUSPENDED
}

// AccountType.java
public enum AccountType {
    SAVINGS, CURRENT
}

// AccountStatus.java
public enum AccountStatus {
    ACTIVE, INACTIVE, FROZEN
}

// TransactionType.java
public enum TransactionType {
    DEPOSIT, WITHDRAWAL, TRANSFER
}

// TransactionStatus.java
public enum TransactionStatus {
    PENDING, COMPLETED, FAILED, REVERSED
}
```

---

#### 2.6 Repository Package (`repository/`)

Spring Data JPA repositories.

```java
// UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByStatus(Status status);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
}

// AccountRepository.java
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUserId(Long userId);
    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);
}

// TransactionRepository.java
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByReferenceId(String referenceId);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
           "ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
           "AND t.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY t.createdAt ASC")
    List<Transaction> findByAccountIdAndDateRange(
        @Param("accountId") Long accountId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}
```

---

#### 2.7 Service Package (`service/`)

Business logic layer with interfaces and implementations.

**Interface Example: `AccountService.java`**
```java
package com.banking.service;

public interface AccountService {
    AccountDTO createAccount(String userEmail, CreateAccountRequest request);
    AccountDTO getAccount(String accountNumber);
    List<AccountDTO> getAccountsByUser(String userEmail);
    BalanceDTO getBalance(String accountNumber);
    TransactionDTO deposit(DepositRequest request);
    TransactionDTO withdraw(WithdrawRequest request);
    TransactionDTO transfer(TransferRequest request);
    AccountStatementDTO getStatement(String accountNumber, LocalDate startDate, LocalDate endDate);
}
```

**Implementation: `AccountServiceImpl.java`**
```java
package com.banking.service.impl;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;
    private final TransactionReferenceGenerator referenceGenerator;

    @Override
    public AccountDTO createAccount(String userEmail, CreateAccountRequest request) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User", userEmail));

        // Check account limit
        if (accountRepository.countByUserId(user.getId()) >= 5) {
            throw new InvalidOperationException("Maximum account limit (5) reached");
        }

        Account account = Account.builder()
            .accountNumber(accountNumberGenerator.generate())
            .accountType(request.accountType())
            .user(user)
            .balance(request.initialDeposit() != null ? request.initialDeposit() : BigDecimal.ZERO)
            .status(AccountStatus.ACTIVE)
            .build();

        account = accountRepository.save(account);
        log.info("Created account {} for user {}", account.getAccountNumber(), userEmail);

        return accountMapper.toDTO(account);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionDTO transfer(TransferRequest request) {
        // Validate same account
        if (request.fromAccountNumber().equals(request.toAccountNumber())) {
            throw new InvalidOperationException("Cannot transfer to the same account");
        }

        // Lock accounts in consistent order to prevent deadlocks
        String first = request.fromAccountNumber().compareTo(request.toAccountNumber()) < 0
            ? request.fromAccountNumber() : request.toAccountNumber();
        String second = first.equals(request.fromAccountNumber())
            ? request.toAccountNumber() : request.fromAccountNumber();

        Account account1 = accountRepository.findByAccountNumberForUpdate(first)
            .orElseThrow(() -> new ResourceNotFoundException("Account", first));
        Account account2 = accountRepository.findByAccountNumberForUpdate(second)
            .orElseThrow(() -> new ResourceNotFoundException("Account", second));

        Account source = account1.getAccountNumber().equals(request.fromAccountNumber())
            ? account1 : account2;
        Account dest = account1.getAccountNumber().equals(request.toAccountNumber())
            ? account1 : account2;

        // Validate accounts are active
        if (source.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(source.getAccountNumber());
        }
        if (dest.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(dest.getAccountNumber());
        }

        // Validate sufficient balance
        if (source.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException(source.getAccountNumber());
        }

        // Execute transfer
        source.setBalance(source.getBalance().subtract(request.amount()));
        dest.setBalance(dest.getBalance().add(request.amount()));

        accountRepository.save(source);
        accountRepository.save(dest);

        // Create transaction record
        Transaction transaction = Transaction.builder()
            .referenceId(referenceGenerator.generate())
            .type(TransactionType.TRANSFER)
            .amount(request.amount())
            .fromAccount(source)
            .toAccount(dest)
            .status(TransactionStatus.COMPLETED)
            .description(request.description())
            .balanceAfter(source.getBalance())
            .build();

        transaction = transactionRepository.save(transaction);
        log.info("Transfer completed: {} from {} to {}",
            request.amount(), source.getAccountNumber(), dest.getAccountNumber());

        return transactionMapper.toDTO(transaction);
    }
}
```

---

#### 2.8 Security Package (`security/`)

JWT and Spring Security components.

```java
// JwtAuthenticationFilter.java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

#### 2.9 Exception Package (`exception/`)

Custom exceptions and global exception handler.

```java
// GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientBalance(
            InsufficientBalanceException ex) {
        log.warn("Insufficient balance: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("INSUFFICIENT_BALANCE", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid"
            ));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("VALIDATION_ERROR", "Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
```

---

### 3. Resources Structure (`src/main/resources`)

#### 3.1 Application Configuration

**`application.yml`**
```yaml
spring:
  application:
    name: digital-banking-system

  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

server:
  port: 8080
  error:
    include-message: always
    include-binding-errors: always

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized

# OpenAPI
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

**`application-dev.yml`**
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521/XEPDB1
    username: banking_user
    password: ${DB_PASSWORD:BankingPass123}
    driver-class-name: oracle.jdbc.OracleDriver
    hikari:
      maximum-pool-size: 10

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  flyway:
    enabled: true
    locations: classpath:db/migration

jwt:
  secret: ${JWT_SECRET:dev-secret-key-minimum-32-characters-long}
  expiration: 3600000
  refresh-expiration: 604800000

logging:
  level:
    com.banking: DEBUG
    org.springframework.security: DEBUG
```

---

### 4. Test Structure (`src/test/java`)

Tests mirror the main source structure with unit and integration tests.

```
test/
└── java/
    └── com/
        └── banking/
            ├── controller/     # @WebMvcTest
            ├── service/        # Unit tests with Mockito
            ├── repository/     # @DataJpaTest
            └── integration/    # @SpringBootTest
```

---

## Build Commands

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Run with coverage
mvn test jacoco:report

# Package JAR
mvn package -DskipTests

# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build Docker image
docker build -t digital-banking-system .

# Run with Docker Compose
docker-compose up -d
```

---

## Quick Start Checklist

1. **Prerequisites:**
   - [ ] JDK 17 installed
   - [ ] Maven 3.9+ installed
   - [ ] Docker installed (for Oracle DB)
   - [ ] IDE (IntelliJ IDEA recommended)

2. **Setup Steps:**
   - [ ] Clone/create project with this structure
   - [ ] Start Oracle database: `docker-compose up -d oracle-db`
   - [ ] Copy `.env.example` to `.env` and configure
   - [ ] Run Flyway migrations: `mvn flyway:migrate`
   - [ ] Build project: `mvn clean compile`
   - [ ] Run tests: `mvn test`
   - [ ] Start application: `mvn spring-boot:run`

3. **Verify:**
   - [ ] Access Swagger UI: `http://localhost:8080/swagger-ui.html`
   - [ ] Health check: `http://localhost:8080/actuator/health`
   - [ ] Test registration API

---

## Implementation Order

Follow this order for building the application:

### Phase 1: Foundation
1. Entity classes with enums
2. Repository interfaces
3. Database migrations (Flyway)
4. Basic configuration files

### Phase 2: Security
5. Security configuration
6. JWT service
7. Authentication filter
8. Custom user details service

### Phase 3: Core Features
9. DTOs (request/response)
10. Mappers
11. Service interfaces
12. Service implementations
13. REST controllers
14. Global exception handler

### Phase 4: Frontend
15. Thymeleaf templates (layout first)
16. Web controllers
17. Static resources (CSS/JS)

### Phase 5: Testing
18. Unit tests (services)
19. Repository tests
20. Controller tests
21. Integration tests

### Phase 6: DevOps
22. Dockerfile
23. Docker Compose
24. GitHub Actions CI/CD
25. README documentation
