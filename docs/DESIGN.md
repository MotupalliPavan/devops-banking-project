# Digital Banking System - Design Document

## 1. Overview

### 1.1 Project Description
A full-stack digital banking application built with Spring Boot backend and Thymeleaf server-side rendered frontend. The system provides core banking functionalities including account management, fund transfers, and transaction history with a focus on security, reliability, and maintainability.

### 1.2 Target Role Alignment
This project is designed to demonstrate skills required for a **Java Backend Developer** position at a digital banking platform, specifically covering:
- Spring Boot and Java expertise
- Layered architecture (Controller, Service, Repository)
- REST API development and integration
- Oracle Database with optimized SQL queries
- Unit testing with JUnit and TDD practices
- Docker containerization and CI/CD pipelines
- Production monitoring and troubleshooting

### 1.3 Scope
**In Scope:**
- User registration and authentication
- Bank account management (create, view, update)
- Fund transfers (internal between accounts)
- Transaction history and statements
- Account balance management
- Basic admin dashboard

**Out of Scope (for simplicity):**
- External payment gateway integration
- Multi-currency support
- Loan management
- Credit card services
- Mobile OTP/2FA (simplified for demo)

---

## 2. Architecture

### 2.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              Thymeleaf SSR Frontend                      │    │
│  │         (HTML Templates + Bootstrap CSS)                 │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                            │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              REST Controllers                            │    │
│  │    - AuthController      - AccountController             │    │
│  │    - TransactionController - UserController              │    │
│  │    - AdminController                                     │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     BUSINESS LAYER                               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                  Services                                │    │
│  │    - AuthService         - AccountService                │    │
│  │    - TransactionService  - UserService                   │    │
│  │    - NotificationService                                 │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PERSISTENCE LAYER                             │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              Repositories (Spring Data JPA)              │    │
│  │    - UserRepository      - AccountRepository             │    │
│  │    - TransactionRepository                               │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     DATABASE LAYER                               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                  Oracle Database                         │    │
│  │    Tables: users, accounts, transactions, audit_logs     │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Layered Architecture Pattern

The application follows the **Standard Spring Boot Layered Architecture**:

| Layer | Responsibility | Components |
|-------|---------------|------------|
| **Presentation** | Handle HTTP requests/responses, input validation | Controllers, DTOs, View Models |
| **Business** | Core business logic, transaction management | Services, Business Rules |
| **Persistence** | Data access, query execution | Repositories, Entities |
| **Database** | Data storage and retrieval | Oracle DB Tables, Indexes |

### 2.3 Design Principles

1. **Separation of Concerns**: Each layer has a single responsibility
2. **Dependency Injection**: Spring IoC container manages dependencies
3. **DTO Pattern**: Separate DTOs for API communication (never expose entities directly)
4. **Repository Pattern**: Abstract data access logic
5. **Service Layer Pattern**: Encapsulate business logic
6. **SOLID Principles**: Single responsibility, Open/closed, etc.

---

## 3. Database Design

### 3.1 Entity Relationship Diagram

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     USERS       │       │    ACCOUNTS     │       │  TRANSACTIONS   │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │──┐    │ id (PK)         │──┐    │ id (PK)         │
│ email           │  │    │ account_number  │  │    │ reference_id    │
│ password        │  │    │ account_type    │  │    │ type            │
│ first_name      │  └───▶│ user_id (FK)    │  │    │ amount          │
│ last_name       │       │ balance         │  └───▶│ from_account_id │
│ phone           │       │ status          │  └───▶│ to_account_id   │
│ role            │       │ created_at      │       │ status          │
│ status          │       │ updated_at      │       │ description     │
│ created_at      │       └─────────────────┘       │ balance_after   │
│ updated_at      │                                 │ created_at      │
└─────────────────┘                                 └─────────────────┘
                                                            │
                          ┌─────────────────┐               │
                          │   AUDIT_LOGS    │◀──────────────┘
                          ├─────────────────┤
                          │ id (PK)         │
                          │ user_id (FK)    │
                          │ action          │
                          │ entity_type     │
                          │ entity_id       │
                          │ old_value       │
                          │ new_value       │
                          │ ip_address      │
                          │ created_at      │
                          └─────────────────┘
```

### 3.2 Table Definitions

#### 3.2.1 USERS Table
```sql
CREATE TABLE users (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR2(255) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    first_name VARCHAR2(100) NOT NULL,
    last_name VARCHAR2(100) NOT NULL,
    phone VARCHAR2(20),
    role VARCHAR2(20) DEFAULT 'CUSTOMER' CHECK (role IN ('CUSTOMER', 'ADMIN')),
    status VARCHAR2(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
```

#### 3.2.2 ACCOUNTS Table
```sql
CREATE TABLE accounts (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_number VARCHAR2(20) NOT NULL UNIQUE,
    account_type VARCHAR2(20) NOT NULL CHECK (account_type IN ('SAVINGS', 'CURRENT')),
    user_id NUMBER NOT NULL REFERENCES users(id),
    balance NUMBER(15,2) DEFAULT 0.00 CHECK (balance >= 0),
    status VARCHAR2(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'FROZEN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_accounts_status ON accounts(status);
```

#### 3.2.3 TRANSACTIONS Table
```sql
CREATE TABLE transactions (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reference_id VARCHAR2(50) NOT NULL UNIQUE,
    type VARCHAR2(20) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
    amount NUMBER(15,2) NOT NULL CHECK (amount > 0),
    from_account_id NUMBER REFERENCES accounts(id),
    to_account_id NUMBER REFERENCES accounts(id),
    status VARCHAR2(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED')),
    description VARCHAR2(500),
    balance_after NUMBER(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_transfer_accounts CHECK (
        (type = 'TRANSFER' AND from_account_id IS NOT NULL AND to_account_id IS NOT NULL) OR
        (type = 'DEPOSIT' AND to_account_id IS NOT NULL) OR
        (type = 'WITHDRAWAL' AND from_account_id IS NOT NULL)
    )
);

CREATE INDEX idx_transactions_from_account ON transactions(from_account_id);
CREATE INDEX idx_transactions_to_account ON transactions(to_account_id);
CREATE INDEX idx_transactions_reference_id ON transactions(reference_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_type ON transactions(type);
```

#### 3.2.4 AUDIT_LOGS Table
```sql
CREATE TABLE audit_logs (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER REFERENCES users(id),
    action VARCHAR2(50) NOT NULL,
    entity_type VARCHAR2(50) NOT NULL,
    entity_id NUMBER,
    old_value CLOB,
    new_value CLOB,
    ip_address VARCHAR2(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
```

### 3.3 Database Normalization
- All tables are in **3NF (Third Normal Form)**
- No transitive dependencies
- Primary keys are auto-generated identity columns
- Foreign keys maintain referential integrity

---

## 4. Component Design

### 4.1 Entity Classes

```java
// User.java
@Entity
@Table(name = "users")
public class User {
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
    private Role role = Role.CUSTOMER;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### 4.2 DTO Classes

```java
// UserDTO.java - For API responses (never expose password)
public record UserDTO(
    Long id,
    String email,
    String firstName,
    String lastName,
    String phone,
    String role,
    String status,
    List<AccountSummaryDTO> accounts
) {}

// CreateUserRequest.java - For registration
public record CreateUserRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    String phone
) {}

// TransferRequest.java - For fund transfers
public record TransferRequest(
    @NotBlank String fromAccountNumber,
    @NotBlank String toAccountNumber,
    @NotNull @Positive BigDecimal amount,
    String description
) {}
```

### 4.3 Service Layer Design

```java
// AccountService.java
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;

    public AccountDTO createAccount(Long userId, CreateAccountRequest request) {
        // 1. Validate user exists
        // 2. Generate unique account number
        // 3. Create account with initial balance
        // 4. Return DTO
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionDTO transfer(TransferRequest request) {
        // 1. Validate accounts exist and are active
        // 2. Check sufficient balance
        // 3. Debit from source account
        // 4. Credit to destination account
        // 5. Create transaction records
        // 6. Return transaction details
    }
}
```

### 4.4 Controller Design

```java
// AccountController.java
@RestController
@RequestMapping("/api/v1/accounts")
@Validated
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccount(accountNumber));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(accountService.createAccount(user.getId(), request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(accountService.transfer(request));
    }
}
```

---

## 5. Security Design

### 5.1 Authentication Flow

```
┌─────────┐     ┌─────────────┐     ┌─────────────┐     ┌──────────┐
│  User   │────▶│  Login API  │────▶│ AuthService │────▶│ Database │
└─────────┘     └─────────────┘     └─────────────┘     └──────────┘
                      │                    │
                      │                    ▼
                      │            ┌─────────────┐
                      │            │  JWT Token  │
                      │            │  Generator  │
                      │            └─────────────┘
                      │                    │
                      ▼                    ▼
              ┌─────────────────────────────────┐
              │     Return JWT + Refresh Token  │
              └─────────────────────────────────┘
```

### 5.2 Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable()) // Disabled for API
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().permitAll())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

### 5.3 Security Best Practices Implemented

| Security Measure | Implementation |
|-----------------|----------------|
| Password Hashing | BCrypt with strength 12 |
| JWT Authentication | Stateless token-based auth |
| Input Validation | Bean Validation (JSR-380) |
| SQL Injection Prevention | Parameterized queries via JPA |
| XSS Prevention | Thymeleaf auto-escaping |
| CORS Configuration | Restricted origins |
| Rate Limiting | API rate limiting (optional) |

---

## 6. Error Handling Design

### 6.1 Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(
            InsufficientBalanceException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("INSUFFICIENT_BALANCE", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage));
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", "Validation failed", errors));
    }
}
```

### 6.2 Custom Exceptions

```java
public class BankingException extends RuntimeException {
    private final String errorCode;

    public BankingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

public class ResourceNotFoundException extends BankingException {
    public ResourceNotFoundException(String resource, String identifier) {
        super("NOT_FOUND", resource + " not found with identifier: " + identifier);
    }
}

public class InsufficientBalanceException extends BankingException {
    public InsufficientBalanceException(String accountNumber) {
        super("INSUFFICIENT_BALANCE",
            "Insufficient balance in account: " + accountNumber);
    }
}

public class AccountNotActiveException extends BankingException {
    public AccountNotActiveException(String accountNumber) {
        super("ACCOUNT_NOT_ACTIVE",
            "Account is not active: " + accountNumber);
    }
}
```

---

## 7. Transaction Management

### 7.1 Fund Transfer Flow

```
┌──────────────────────────────────────────────────────────────────┐
│                    FUND TRANSFER PROCESS                          │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  1. Receive Transfer Request                                      │
│         │                                                         │
│         ▼                                                         │
│  2. Validate Request (accounts exist, active, sufficient funds)   │
│         │                                                         │
│         ▼                                                         │
│  3. BEGIN TRANSACTION (SERIALIZABLE ISOLATION)                    │
│         │                                                         │
│         ├──▶ 4. Lock source account (SELECT FOR UPDATE)           │
│         │                                                         │
│         ├──▶ 5. Lock destination account (SELECT FOR UPDATE)      │
│         │                                                         │
│         ├──▶ 6. Debit source account                              │
│         │                                                         │
│         ├──▶ 7. Credit destination account                        │
│         │                                                         │
│         ├──▶ 8. Create transaction record                         │
│         │                                                         │
│         ├──▶ 9. Create audit log                                  │
│         │                                                         │
│         ▼                                                         │
│  10. COMMIT TRANSACTION                                           │
│         │                                                         │
│         ▼                                                         │
│  11. Return Success Response                                      │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

### 7.2 Transaction Isolation

For financial transactions, we use **SERIALIZABLE** isolation level to prevent:
- Dirty reads
- Non-repeatable reads
- Phantom reads
- Lost updates

```java
@Transactional(isolation = Isolation.SERIALIZABLE,
               rollbackFor = Exception.class)
public TransactionDTO transfer(TransferRequest request) {
    // Implementation with pessimistic locking
}
```

---

## 8. Testing Strategy

### 8.1 Testing Pyramid

```
                    ┌───────────┐
                    │    E2E    │  ← Few (Selenium/Playwright)
                   ┌┴───────────┴┐
                   │ Integration │  ← Some (@SpringBootTest)
                  ┌┴─────────────┴┐
                  │   Unit Tests   │  ← Many (JUnit + Mockito)
                  └────────────────┘
```

### 8.2 Test Coverage Goals

| Layer | Target Coverage | Testing Approach |
|-------|-----------------|------------------|
| Service | 85%+ | Unit tests with Mockito |
| Repository | 80%+ | @DataJpaTest with H2 |
| Controller | 80%+ | @WebMvcTest with MockMvc |
| Integration | Key flows | @SpringBootTest |

### 8.3 TDD Approach

```java
// Example: TDD for AccountService.transfer()

// RED: Write failing test first
@Test
void transfer_shouldDebitSourceAndCreditDestination() {
    // Given
    Account source = createAccount("1001", new BigDecimal("1000"));
    Account dest = createAccount("1002", new BigDecimal("500"));
    TransferRequest request = new TransferRequest("1001", "1002",
        new BigDecimal("200"), "Test transfer");

    when(accountRepository.findByAccountNumber("1001"))
        .thenReturn(Optional.of(source));
    when(accountRepository.findByAccountNumber("1002"))
        .thenReturn(Optional.of(dest));

    // When
    TransactionDTO result = accountService.transfer(request);

    // Then
    assertThat(result.status()).isEqualTo("COMPLETED");
    assertThat(source.getBalance()).isEqualByComparingTo("800");
    assertThat(dest.getBalance()).isEqualByComparingTo("700");
}

// GREEN: Write minimal code to pass
// REFACTOR: Improve code quality
```

---

## 9. Deployment Architecture

### 9.1 Container Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Compose / Kubernetes               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                  Application Container               │    │
│  │  ┌─────────────────────────────────────────────┐    │    │
│  │  │         Spring Boot Application              │    │    │
│  │  │         (digital-banking-system:latest)      │    │    │
│  │  │         Port: 8080                           │    │    │
│  │  └─────────────────────────────────────────────┘    │    │
│  └─────────────────────────────────────────────────────┘    │
│                            │                                 │
│                            ▼                                 │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                  Database Container                  │    │
│  │  ┌─────────────────────────────────────────────┐    │    │
│  │  │         Oracle Database XE                   │    │    │
│  │  │         (gvenzl/oracle-xe:21-slim)           │    │    │
│  │  │         Port: 1521                           │    │    │
│  │  └─────────────────────────────────────────────┘    │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### 9.2 CI/CD Pipeline

```
┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
│  Push   │───▶│  Build  │───▶│  Test   │───▶│  Build  │───▶│ Deploy  │
│  Code   │    │  Maven  │    │  JUnit  │    │  Docker │    │  K8s    │
└─────────┘    └─────────┘    └─────────┘    └─────────┘    └─────────┘
     │              │              │              │              │
     ▼              ▼              ▼              ▼              ▼
 GitHub        Compile &      Unit &        Push to       kubectl
 Actions       Package       Integration    GHCR          apply
                              Tests
```

---

## 10. Monitoring & Observability

### 10.1 Monitoring Stack

| Tool | Purpose |
|------|---------|
| **Spring Actuator** | Health checks, metrics endpoints |
| **Micrometer** | Metrics collection |
| **Grafana** | Dashboards and visualization |
| **Prometheus** | Metrics storage (optional) |
| **Jaeger** | Distributed tracing (optional) |

### 10.2 Key Metrics to Monitor

- Request latency (p50, p95, p99)
- Error rate
- Transaction success/failure rate
- Database connection pool usage
- JVM memory and GC metrics
- Active user sessions

### 10.3 Health Check Endpoints

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

---

## 11. Non-Functional Requirements

| Requirement | Target | Implementation |
|-------------|--------|----------------|
| **Availability** | 99.9% | Health checks, graceful shutdown |
| **Response Time** | < 200ms (p95) | Query optimization, caching |
| **Throughput** | 100 TPS | Connection pooling, async processing |
| **Security** | OWASP Top 10 | Input validation, parameterized queries |
| **Scalability** | Horizontal | Stateless design, containerization |

---

## 12. Future Enhancements (Out of Current Scope)

1. **Microservices Migration**: Split into separate services
2. **Event Sourcing**: For complete transaction audit trail
3. **Caching Layer**: Redis for session and data caching
4. **Message Queue**: For async notifications
5. **Multi-Factor Authentication**: OTP via SMS/Email
6. **API Versioning**: Support multiple API versions

---

## References

- [Spring Boot Layered Architecture](https://medium.com/@RogelioOrts/layered-architecture-spring-boot-af7dc071d2b5)
- [Banking Application Best Practices](https://medium.com/@aliihsanhashas1/core-banking-application-with-spring-boot-microservices-architecture-and-immutability-principles-5b4ceb91d96a)
- [Oracle Database with Spring Boot](https://blogs.oracle.com/developers/post/ucp-best-practices-for-oracle-database-19c-and-spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JUnit 5 Testing](https://www.javaguides.net/2024/09/unit-testing-spring-boot-service-layer.html)
