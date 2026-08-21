# Digital Banking System - Tech Stack Documentation

## Overview

This document provides a comprehensive guide to the technology stack used in the Digital Banking System, including justifications, configurations, and best practices for each component.

---

## Table of Contents

1. [Tech Stack Summary](#1-tech-stack-summary)
2. [Core Framework](#2-core-framework)
3. [Database](#3-database)
4. [Security](#4-security)
5. [Frontend](#5-frontend)
6. [Testing](#6-testing)
7. [DevOps & Deployment](#7-devops--deployment)
8. [Monitoring & Logging](#8-monitoring--logging)
9. [Development Tools](#9-development-tools)
10. [Dependencies Reference](#10-dependencies-reference)

---

## 1. Tech Stack Summary

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Language** | Java | 17 (LTS) | Core programming language |
| **Framework** | Spring Boot | 3.2.x | Application framework |
| **Database** | Oracle Database | 21c XE | Primary data storage |
| **ORM** | Spring Data JPA / Hibernate | 6.x | Object-relational mapping |
| **Security** | Spring Security + JWT | 6.x | Authentication & Authorization |
| **Frontend** | Thymeleaf | 3.x | Server-side rendering |
| **Styling** | Bootstrap | 5.3 | CSS framework |
| **Build Tool** | Maven | 3.9.x | Dependency management |
| **Testing** | JUnit 5 + Mockito | 5.x / 5.x | Unit & integration testing |
| **Containerization** | Docker | 24.x | Application containerization |
| **CI/CD** | GitHub Actions | - | Continuous integration |
| **API Documentation** | SpringDoc OpenAPI | 2.x | Swagger/OpenAPI docs |

---

## 2. Core Framework

### 2.1 Java 17 (LTS)

**Why Java 17?**
- Long-Term Support (LTS) version with support until 2029
- Required for Spring Boot 3.x
- Modern language features: Records, Pattern Matching, Sealed Classes
- Performance improvements over previous versions

**Key Features Used:**
```java
// Records for DTOs (immutable data carriers)
public record UserDTO(
    Long id,
    String email,
    String firstName,
    String lastName
) {}

// Pattern matching for instanceof
if (exception instanceof ResourceNotFoundException e) {
    return handleNotFound(e);
}

// Text blocks for SQL queries
String query = """
    SELECT u.* FROM users u
    WHERE u.status = 'ACTIVE'
    AND u.role = :role
    """;
```

---

### 2.2 Spring Boot 3.2.x

**Why Spring Boot?**
- Industry standard for Java enterprise applications
- Auto-configuration reduces boilerplate
- Embedded server (no external Tomcat needed)
- Production-ready features (actuator, metrics)
- Excellent documentation and community support

**Spring Boot Starters Used:**

| Starter | Purpose |
|---------|---------|
| `spring-boot-starter-web` | REST API development |
| `spring-boot-starter-data-jpa` | Database access with JPA |
| `spring-boot-starter-security` | Security framework |
| `spring-boot-starter-validation` | Bean validation |
| `spring-boot-starter-thymeleaf` | Template engine |
| `spring-boot-starter-actuator` | Production monitoring |
| `spring-boot-starter-test` | Testing support |

**Configuration Example (`application.yml`):**
```yaml
spring:
  application:
    name: digital-banking-system

  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.OracleDialect

server:
  port: 8080
  error:
    include-message: always
    include-binding-errors: always
```

---

### 2.3 Spring Data JPA

**Why Spring Data JPA?**
- Reduces boilerplate for data access
- Query derivation from method names
- Pagination and sorting out of the box
- Easy integration with Spring transactions

**Repository Example:**
```java
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserIdAndStatus(Long userId, Status status);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    List<Account> findAllByUserId(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(
        @Param("accountNumber") String accountNumber);
}
```

---

## 3. Database

### 3.1 Oracle Database 21c XE

**Why Oracle?**
- Matches IDFC FIRST Bank's production environment
- Enterprise-grade reliability and performance
- ACID compliance for financial transactions
- Advanced features: partitioning, compression, analytics

**Connection Configuration:**
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@${DB_HOST:localhost}:${DB_PORT:1521}/${DB_SERVICE:XEPDB1}
    username: ${DB_USERNAME:banking_user}
    password: ${DB_PASSWORD}
    driver-class-name: oracle.jdbc.OracleDriver

    # HikariCP Connection Pool
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      pool-name: BankingHikariPool
      max-lifetime: 1200000
      connection-timeout: 20000
      validation-timeout: 5000

  jpa:
    database-platform: org.hibernate.dialect.OracleDialect
```

**Docker Compose for Oracle:**
```yaml
version: '3.8'
services:
  oracle-db:
    image: gvenzl/oracle-xe:21-slim
    container_name: oracle-banking
    ports:
      - "1521:1521"
    environment:
      ORACLE_PASSWORD: ${ORACLE_PASSWORD:-SecurePass123}
      APP_USER: banking_user
      APP_USER_PASSWORD: ${APP_USER_PASSWORD:-BankingPass123}
    volumes:
      - oracle_data:/opt/oracle/oradata
    healthcheck:
      test: ["CMD", "healthcheck.sh"]
      interval: 30s
      timeout: 10s
      retries: 5

volumes:
  oracle_data:
```

**Oracle-Specific SQL Features:**
```sql
-- Identity columns (Oracle 12c+)
CREATE TABLE users (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ...
);

-- Sequence for account numbers
CREATE SEQUENCE account_number_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE;

-- Optimized query with hints
SELECT /*+ INDEX(t idx_transactions_created_at) */
    t.* FROM transactions t
WHERE t.created_at BETWEEN :startDate AND :endDate;
```

---

### 3.2 Database Migration (Flyway)

**Why Flyway?**
- Version control for database schema
- Automatic migration on startup
- Rollback support
- CI/CD friendly

**Configuration:**
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

**Migration File Naming:**
```
src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_accounts_table.sql
├── V3__create_transactions_table.sql
├── V4__create_audit_logs_table.sql
└── V5__add_indexes.sql
```

---

## 4. Security

### 4.1 Spring Security 6.x

**Why Spring Security?**
- De facto standard for Java security
- Comprehensive authentication/authorization
- Protection against common vulnerabilities
- Seamless Spring Boot integration

**Security Configuration:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

---

### 4.2 JWT (JSON Web Tokens)

**Why JWT?**
- Stateless authentication (scalable)
- Self-contained tokens
- Industry standard
- Works well with microservices

**Dependencies:**
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

**JWT Service Implementation:**
```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // 1 hour

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration; // 7 days

    public String generateToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> claims,
                              UserDetails userDetails,
                              long expiration) {
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
```

**JWT Configuration:**
```yaml
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-min-32-chars}
  expiration: 3600000      # 1 hour in milliseconds
  refresh-expiration: 604800000  # 7 days in milliseconds
```

---

## 5. Frontend

### 5.1 Thymeleaf

**Why Thymeleaf?**
- Natural templating (valid HTML)
- Excellent Spring Boot integration
- Server-side rendering (good for SEO)
- Simple learning curve

**Template Example:**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <title>Dashboard - Digital Banking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-dark bg-primary">
        <span class="navbar-brand">Digital Banking</span>
        <span sec:authentication="name" class="text-white"></span>
    </nav>

    <div class="container mt-4">
        <h2>Your Accounts</h2>
        <div class="row">
            <div th:each="account : ${accounts}" class="col-md-4 mb-3">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title" th:text="${account.accountType}">Savings</h5>
                        <p class="card-text">
                            Account: <span th:text="${account.accountNumber}"></span><br>
                            Balance: <span th:text="${#numbers.formatCurrency(account.balance)}"></span>
                        </p>
                        <a th:href="@{/accounts/{id}(id=${account.id})}"
                           class="btn btn-primary">View Details</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
```

---

### 5.2 Bootstrap 5.3

**Why Bootstrap?**
- Rapid UI development
- Responsive design out of the box
- Extensive component library
- No custom CSS required for basic UI

**CDN Setup:**
```html
<!-- CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet">

<!-- JS (optional, for interactive components) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
```

---

## 6. Testing

### 6.1 JUnit 5

**Why JUnit 5?**
- Modern Java testing framework
- Parameterized tests
- Better assertions
- Spring Boot default

**Test Example:**
```java
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Should transfer money between accounts successfully")
    void transfer_shouldDebitSourceAndCreditDestination() {
        // Given
        Account source = createAccount("1001", new BigDecimal("1000"));
        Account dest = createAccount("1002", new BigDecimal("500"));
        TransferRequest request = new TransferRequest(
            "1001", "1002", new BigDecimal("200"), "Test");

        when(accountRepository.findByAccountNumberForUpdate("1001"))
            .thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumberForUpdate("1002"))
            .thenReturn(Optional.of(dest));

        // When
        TransactionDTO result = accountService.transfer(request);

        // Then
        assertThat(result.status()).isEqualTo("COMPLETED");
        assertThat(source.getBalance()).isEqualByComparingTo("800");
        assertThat(dest.getBalance()).isEqualByComparingTo("700");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance")
    void transfer_shouldThrowExceptionWhenInsufficientBalance() {
        // Given
        Account source = createAccount("1001", new BigDecimal("100"));
        TransferRequest request = new TransferRequest(
            "1001", "1002", new BigDecimal("500"), "Test");

        when(accountRepository.findByAccountNumberForUpdate("1001"))
            .thenReturn(Optional.of(source));

        // When & Then
        assertThatThrownBy(() -> accountService.transfer(request))
            .isInstanceOf(InsufficientBalanceException.class)
            .hasMessageContaining("Insufficient balance");
    }
}
```

---

### 6.2 Mockito

**Why Mockito?**
- Simple mocking API
- Spring Boot default
- Excellent documentation
- Annotation-based configuration

**Key Annotations:**
```java
@Mock           // Create a mock object
@InjectMocks    // Inject mocks into the tested class
@Spy            // Partial mock (real methods unless stubbed)
@Captor         // Capture argument values
```

---

### 6.3 Spring Boot Test

**Test Slices:**

| Annotation | Purpose | Loads |
|------------|---------|-------|
| `@SpringBootTest` | Full integration test | Entire context |
| `@WebMvcTest` | Controller layer test | Web layer only |
| `@DataJpaTest` | Repository test | JPA components |
| `@RestClientTest` | REST client test | REST client |

**Controller Test Example:**
```java
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    @WithMockUser(username = "user@test.com", roles = "CUSTOMER")
    void getAccount_shouldReturnAccountDetails() throws Exception {
        // Given
        AccountDTO account = new AccountDTO(1L, "1234567890", "SAVINGS",
            new BigDecimal("50000"), "ACTIVE");
        when(accountService.getAccount("1234567890")).thenReturn(account);

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/1234567890"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accountNumber").value("1234567890"))
            .andExpect(jsonPath("$.data.balance").value(50000));
    }
}
```

**Repository Test Example:**
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByAccountNumber_shouldReturnAccount() {
        // Given
        User user = createAndPersistUser();
        Account account = new Account();
        account.setAccountNumber("1234567890");
        account.setUser(user);
        account.setBalance(new BigDecimal("1000"));
        entityManager.persistAndFlush(account);

        // When
        Optional<Account> found = accountRepository.findByAccountNumber("1234567890");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualByComparingTo("1000");
    }
}
```

---

## 7. DevOps & Deployment

### 7.1 Docker

**Why Docker?**
- Consistent environments (dev, test, prod)
- Easy deployment
- Isolation
- Works with Kubernetes

**Dockerfile:**
```dockerfile
# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Docker Compose (Full Stack):**
```yaml
version: '3.8'

services:
  app:
    build: .
    container_name: banking-app
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=oracle-db
      - DB_PORT=1521
      - DB_SERVICE=XEPDB1
      - DB_USERNAME=banking_user
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      oracle-db:
        condition: service_healthy
    networks:
      - banking-network

  oracle-db:
    image: gvenzl/oracle-xe:21-slim
    container_name: oracle-banking
    ports:
      - "1521:1521"
    environment:
      - ORACLE_PASSWORD=${ORACLE_PASSWORD}
      - APP_USER=banking_user
      - APP_USER_PASSWORD=${DB_PASSWORD}
    volumes:
      - oracle_data:/opt/oracle/oradata
    healthcheck:
      test: ["CMD", "healthcheck.sh"]
      interval: 30s
      timeout: 10s
      retries: 5
    networks:
      - banking-network

networks:
  banking-network:
    driver: bridge

volumes:
  oracle_data:
```

---

### 7.2 GitHub Actions CI/CD

**Why GitHub Actions?**
- Integrated with GitHub
- Free for public repos
- Easy configuration
- Good marketplace of actions

**CI/CD Pipeline (`.github/workflows/ci.yml`):**
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  JAVA_VERSION: '17'
  REGISTRY: ghcr.io
  IMAGE_NAME: ${{ github.repository }}

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: 'maven'

      - name: Build with Maven
        run: mvn clean compile -B

  test:
    runs-on: ubuntu-latest
    needs: build

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: 'maven'

      - name: Run tests
        run: mvn test -B

      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-results
          path: target/surefire-reports/

      - name: Generate coverage report
        run: mvn jacoco:report

      - name: Upload coverage report
        uses: actions/upload-artifact@v4
        with:
          name: coverage-report
          path: target/site/jacoco/

  build-docker:
    runs-on: ubuntu-latest
    needs: test
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: 'maven'

      - name: Build JAR
        run: mvn package -DskipTests -B

      - name: Log in to Container Registry
        uses: docker/login-action@v3
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push Docker image
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: |
            ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:latest
            ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}
```

---

### 7.3 Kubernetes (Optional)

**Deployment Manifest:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: banking-app
  labels:
    app: banking
spec:
  replicas: 2
  selector:
    matchLabels:
      app: banking
  template:
    metadata:
      labels:
        app: banking
    spec:
      containers:
        - name: banking-app
          image: ghcr.io/username/digital-banking-system:latest
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "k8s"
            - name: DB_HOST
              valueFrom:
                secretKeyRef:
                  name: banking-secrets
                  key: db-host
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "500m"
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: banking-service
spec:
  selector:
    app: banking
  ports:
    - port: 80
      targetPort: 8080
  type: LoadBalancer
```

---

## 8. Monitoring & Logging

### 8.1 Spring Boot Actuator

**Configuration:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true
```

**Key Endpoints:**
| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application information |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus format metrics |

---

### 8.2 Logging (SLF4J + Logback)

**Logging Configuration (`logback-spring.xml`):**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProperty scope="context" name="APP_NAME" source="spring.application.name"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/${APP_NAME}.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/${APP_NAME}.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.banking" level="DEBUG"/>
    <logger name="org.springframework" level="INFO"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

---

## 9. Development Tools

### 9.1 IDE: IntelliJ IDEA

**Recommended Plugins:**
- Spring Boot Assistant
- Lombok
- SonarLint
- Docker
- Database Tools

---

### 9.2 API Documentation: SpringDoc OpenAPI

**Dependency:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**Configuration:**
```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operationsSorter: method
    tagsSorter: alpha
```

**Usage:**
```java
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Management", description = "APIs for managing bank accounts")
public class AccountController {

    @Operation(
        summary = "Get account by account number",
        description = "Retrieves detailed information about a specific bank account"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account found"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccount(
            @Parameter(description = "Account number") @PathVariable String accountNumber) {
        // Implementation
    }
}
```

---

## 10. Dependencies Reference

### Complete `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.2</version>
        <relativePath/>
    </parent>

    <groupId>com.banking</groupId>
    <artifactId>digital-banking-system</artifactId>
    <version>1.0.0</version>
    <name>Digital Banking System</name>
    <description>Digital Banking Application with Spring Boot</description>

    <properties>
        <java.version>17</java.version>
        <jjwt.version>0.12.3</jjwt.version>
        <springdoc.version>2.3.0</springdoc.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Thymeleaf Security Extras -->
        <dependency>
            <groupId>org.thymeleaf.extras</groupId>
            <artifactId>thymeleaf-extras-springsecurity6</artifactId>
        </dependency>

        <!-- Oracle Database -->
        <dependency>
            <groupId>com.oracle.database.jdbc</groupId>
            <artifactId>ojdbc11</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- API Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>

        <!-- Database Migration -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-oracle</artifactId>
        </dependency>

        <!-- Lombok (Optional - reduces boilerplate) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- DevTools (Development only) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.11</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Version Compatibility Matrix

| Component | Version | Compatibility Notes |
|-----------|---------|---------------------|
| Java | 17 | Required for Spring Boot 3.x |
| Spring Boot | 3.2.x | Latest stable |
| Spring Security | 6.x | Bundled with Spring Boot 3.x |
| Hibernate | 6.x | Bundled with Spring Boot 3.x |
| Oracle JDBC | 21.x (ojdbc11) | For Oracle 19c/21c |
| JUnit | 5.x | Bundled with spring-boot-starter-test |
| Mockito | 5.x | Bundled with spring-boot-starter-test |

---

## References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Oracle JDBC Driver Documentation](https://docs.oracle.com/en/database/oracle/oracle-database/21/jjdbc/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Docker Documentation](https://docs.docker.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
