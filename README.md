# Digital Banking System

A comprehensive digital banking platform built with Spring Boot 3.4.1, featuring account management, fund transfers, JWT authentication, and a Thymeleaf-based web interface.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![License](https://img.shields.io/badge/License-Educational-yellow)

## Features

### User Features
- **User Registration & Login**: Secure authentication with form validation
- **Auto Account Creation**: SAVINGS account automatically created on registration
- **Profile Management**: View and manage personal information
- **Change Password**: Secure password change with strength indicator

### Banking Features
- **Account Dashboard**: View account balance and recent transactions
- **Deposits**: Add money to your account
- **Withdrawals**: Withdraw money with balance validation
- **Fund Transfers**: Transfer money to other accounts
- **Transaction History**: View all transactions with pagination

### Security Features
- **JWT Authentication**: Secure API endpoints with JSON Web Tokens
- **Form-based Authentication**: Secure web interface login
- **Password Encryption**: BCrypt password hashing
- **CSRF Protection**: Cross-Site Request Forgery protection

### UI Features
- **Responsive Design**: Bootstrap 5 based responsive UI
- **Indian Rupee (₹)**: Currency display in Indian Rupee
- **Client-side Validation**: Real-time form validation
- **Password Toggle**: Show/hide password functionality
- **Password Strength Indicator**: Visual password strength meter

## Tech Stack

| Category | Technology |
|----------|------------|
| **Backend** | Spring Boot 3.4.1, Spring Security, Spring Data JPA |
| **Frontend** | Thymeleaf, Bootstrap 5, Bootstrap Icons |
| **Database** | PostgreSQL 15 (Production), H2 (Testing) |
| **Authentication** | JWT (JSON Web Tokens) |
| **Build Tool** | Maven |
| **Containerization** | Docker, Docker Compose |

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 15+ (or use Docker)
- (Optional) Docker & Docker Compose

## Quick Start

### Option 1: Using Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/Satish-Das/digital-banking-system.git
cd digital-banking-system

# Start with Docker Compose
docker-compose up --build
```

Access the application at http://localhost:8080

### Option 2: Running Locally

1. **Setup PostgreSQL Database**:
```sql
CREATE DATABASE digitalbank;
```

2. **Configure Database** (if not using default):
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/digitalbank
spring.datasource.username=postgres
spring.datasource.password=your_password
```

3. **Run the application**:
```bash
mvn spring-boot:run
```

4. **Access the application**:
   - Web UI: http://localhost:8080
   - API Base URL: http://localhost:8080/api/v1

## Default Configuration

| Property | Value |
|----------|-------|
| Server Port | 8080 |
| Database | PostgreSQL on localhost:5432 |
| Database Name | digitalbank |
| JWT Expiration | 24 hours |

## Web Pages

| Page | URL | Description |
|------|-----|-------------|
| Login | `/login` | User login page |
| Register | `/register` | New user registration |
| Dashboard | `/dashboard` | Account overview and quick actions |
| My Account | `/accounts` | Account details |
| Transactions | `/transactions` | Transaction history |
| Transfer | `/transactions/transfer` | Fund transfer |
| Deposit | `/transactions/deposit` | Deposit money |
| Withdraw | `/transactions/withdraw` | Withdraw money |
| Profile | `/profile` | User profile |
| Change Password | `/profile/change-password` | Change password |

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login and get JWT token |
| POST | `/api/v1/auth/refresh` | Refresh JWT token |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users/me` | Get current user profile |
| PUT | `/api/v1/users/me` | Update profile |
| POST | `/api/v1/users/me/change-password` | Change password |

### Accounts
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/accounts` | Get user's accounts |
| GET | `/api/v1/accounts/{accountNumber}` | Get account details |
| GET | `/api/v1/accounts/{accountNumber}/balance` | Get balance |
| GET | `/api/v1/accounts/{accountNumber}/statement` | Get statement |

### Transactions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/transactions/deposit` | Deposit money |
| POST | `/api/v1/transactions/withdraw` | Withdraw money |
| POST | `/api/v1/transactions/transfer` | Transfer funds |
| GET | `/api/v1/transactions` | Get transaction history |

### Admin (requires ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/admin/users` | Get all users |
| POST | `/api/v1/admin/users/{id}/deactivate` | Deactivate user |
| POST | `/api/v1/admin/users/{id}/activate` | Activate user |
| POST | `/api/v1/admin/accounts/{accountNumber}/freeze` | Freeze account |
| POST | `/api/v1/admin/accounts/{accountNumber}/unfreeze` | Unfreeze account |

## Project Structure

```
src/main/java/com/digitalbanking/digital_banking_system/
├── config/          # Configuration classes (Security, etc.)
├── controller/      # REST API controllers
│   └── web/         # Thymeleaf web controllers
├── dto/             # Data Transfer Objects
│   ├── request/     # Request DTOs
│   └── response/    # Response DTOs
├── entity/          # JPA entities (User, Account, Transaction)
├── enums/           # Enumerations (Role, Status, TransactionType)
├── exception/       # Custom exceptions & GlobalExceptionHandler
├── mapper/          # Entity-DTO mappers
├── repository/      # JPA repositories
├── security/        # JWT & Security components
├── service/         # Service interfaces
│   └── impl/        # Service implementations
└── util/            # Utility classes

src/main/resources/
├── templates/       # Thymeleaf HTML templates
│   ├── auth/        # Login, Register pages
│   ├── accounts/    # Account pages
│   ├── transactions/# Transaction pages
│   ├── fragments/   # Reusable fragments
│   └── layout/      # Layout templates
├── static/          # Static resources (CSS, JS)
└── application.properties
```

## Screenshots

### Dashboard
- View account balance
- Quick actions (Deposit, Withdraw, Transfer)
- Recent transactions

### Transaction Pages
- Real-time form validation
- Balance checking before withdrawal/transfer
- Transaction summary before confirmation

### Profile & Security
- View personal information
- Change password with strength indicator

## Docker Configuration

The application includes Docker support with:
- **PostgreSQL 15 Alpine**: Lightweight database container
- **Spring Boot App**: Application container
- **Health Checks**: Automatic health monitoring
- **Persistent Volume**: Data persistence for PostgreSQL

```yaml
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database URL | jdbc:postgresql://localhost:5432/digitalbank |
| `SPRING_DATASOURCE_USERNAME` | Database username | postgres |
| `SPRING_DATASOURCE_PASSWORD` | Database password | 1234 |
| `JWT_SECRET` | JWT signing key | (generated) |


# Digital Banking CI/CD Pipeline

## Project Overview

**CI/CD pipeline for a Digital Banking Spring Boot application**.

The pipeline automates the complete software delivery process from source-code checkout through application deployment and health validation.

### CI/CD Flow

```text
GitHub
   |
   v
Jenkins
   |
   +--> Compile
   |
   +--> Unit Tests
   |
   +--> SonarQube Analysis
   |
   +--> Quality Gate
   |
   +--> Maven Package
   |
   +--> Docker Build
   |
   +--> Docker Hub Push
   |
   +--> Docker Compose Deploy
   |
   +--> Health Check
   |
   v
Digital Banking Application
   |
   v
PostgreSQL
```

---

## Technologies Used

- Java 17
- Spring Boot 3.4.1
- Maven
- Git / GitHub
- Jenkins
- SonarQube
- Docker
- Docker Compose
- Docker Hub
- PostgreSQL 15
- Linux / Ubuntu
- AWS EC2
- Bash

---

## Application Components

The deployment consists of two Docker containers:

### 1. Digital Banking Application

```text
Container: digital-banking-app
Image: motupallipavan/digital-banking-cicd-app
Port: 8081 -> 8080
```

The Spring Boot application runs inside the Docker container on port `8080`.

The host exposes the application on port `8081`.

Application URL:

```text
http://<EC2-PUBLIC-IP>:8081/login
```

### 2. PostgreSQL Database

```text
Container: digital-banking-postgres
Image: postgres:15-alpine
Port: 5432
Database: digitalbank
```

The application connects to PostgreSQL using the Docker Compose service name:

```text
jdbc:postgresql://postgres:5432/digitalbank
```

---

# Jenkins CI/CD Pipeline

The Jenkins pipeline contains the following stages:

```text
Checkout
   ↓
Verify Environment
   ↓
Compile
   ↓
Test
   ↓
SonarQube Analysis
   ↓
Quality Gate
   ↓
Package
   ↓
Docker Build
   ↓
Docker Hub Push
   ↓
Deploy
   ↓
Health Check
```

---

## 1. Checkout

Jenkins checks out the source code from the configured Git repository.

```groovy
stage('Checkout') {
    steps {
        checkout scm
    }
}
```

---

## 2. Verify Environment

The pipeline verifies that the required tools are available on the Jenkins agent.

```bash
java -version
mvn -version
docker --version
docker compose version
```

This helps identify environment or tool-version problems before the build starts.

---

## 3. Compile

Maven compiles the Java application.

```bash
mvn clean compile
```

If compilation fails, the pipeline stops before continuing to later stages.

---

## 4. Test

Unit and application tests are executed using Maven.

```bash
mvn test
```

Jenkins collects the generated Surefire test reports:

```text
**/target/surefire-reports/*.xml
```

---

## 5. SonarQube Analysis

The project is analyzed using SonarQube.

The pipeline uses the SonarQube Maven scanner:

```bash
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar
```

The Jenkins SonarQube installation is configured with the name:

```text
SonarQube
```

SonarQube provides code-quality analysis before the application is packaged and deployed.

---

## 6. Quality Gate

After SonarQube analysis, Jenkins waits for the SonarQube Quality Gate result.

```groovy
timeout(time: 10, unit: 'MINUTES') {
    waitForQualityGate abortPipeline: true
}
```

If the Quality Gate fails, the pipeline stops and the application is not deployed.

This prevents code that does not meet the configured quality requirements from reaching deployment.

---

## 7. Package

Maven packages the application.

```bash
mvn package -DskipTests
```

The resulting application JAR is used for the Docker image.

---

## 8. Docker Build

Jenkins builds a Docker image using the project Dockerfile.

The image is tagged using the Jenkins build number:

```text
motupallipavan/digital-banking-cicd-app:${BUILD_NUMBER}
```

For example:

```text
motupallipavan/digital-banking-cicd-app:25
```

This provides a unique version for every Jenkins build.

---

## 9. Docker Hub Push

Jenkins authenticates with Docker Hub using Jenkins credentials.

Credential ID:

```text
dockerhub-credentials
```

The pipeline pushes the versioned image:

```text
motupallipavan/digital-banking-cicd-app:25
```

It also updates:

```text
motupallipavan/digital-banking-cicd-app:latest
```

Versioned tags make it possible to identify exactly which Jenkins build produced an image.

---

# Docker Compose Deployment

Docker Compose manages the application and PostgreSQL containers.

Start the deployment with:

```bash
IMAGE_TAG=25 docker compose up -d
```

Stop the existing deployment with:

```bash
docker compose down --remove-orphans
```

The Jenkins deployment stage performs both operations automatically.

```bash
docker compose down --remove-orphans || true
IMAGE_TAG=${IMAGE_TAG} docker compose up -d
```

The `--remove-orphans` option helps remove containers that are no longer defined in the current Compose configuration.

---

# Database Configuration

PostgreSQL is configured with:

```text
Database: digitalbank
Username: postgres
Password: 1234
```

The application uses:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/digitalbank
```

The important point is that the application connects to the PostgreSQL **Compose service name**:

```text
postgres
```

rather than using `localhost`.

---

# Container Health Check

The Digital Banking application has a Docker health check:

```text
http://localhost:8080/login
```

Jenkins checks the container health status:

```bash
docker inspect --format='{{.State.Health.Status}}' digital-banking-app
```

The pipeline waits for the application to become:

```text
healthy
```

After the container becomes healthy, Jenkins validates the endpoint:

```bash
curl --fail --silent --show-error \
http://localhost:8081/login
```

If the application becomes unhealthy or does not become healthy within the configured timeout, the pipeline fails.

---

# Deployment Verification

Check running containers:

```bash
docker compose ps
```

Expected result:

```text
digital-banking-app        Up ... (healthy)
digital-banking-postgres  Up ... (healthy)
```

Check application logs:

```bash
docker compose logs app
```

Check PostgreSQL logs:

```bash
docker compose logs postgres
```

Follow application logs:

```bash
docker compose logs -f app
```

---

# Jenkins Credentials

## Docker Hub

Create a Jenkins username/password credential.

Use:

```text
ID: dockerhub-credentials
```

The username should be your Docker Hub username.

Use a Docker Hub access token as the password rather than storing your Docker Hub account password in Jenkins.

## SonarQube

Configure the SonarQube server in Jenkins with the installation name:

```text
SonarQube
```

The Jenkins pipeline references this installation using:

```groovy
withSonarQubeEnv("SonarQube")
```

---

# Troubleshooting

## SonarQube Connection Failed

If Jenkins reports:

```text
Failed to query server version
```

verify that SonarQube is running:

```bash
docker ps
```

Check port `9000`:

```bash
sudo ss -lntp | grep 9000
```

Test connectivity:

```bash
curl http://localhost:9000
```

Check SonarQube logs:

```bash
docker logs <sonarqube-container>
```

---

## SonarQube Task Stuck in PENDING

If Jenkins remains at:

```text
Checking status of SonarQube task
status is 'PENDING'
```

check:

- SonarQube container status
- SonarQube system resources
- Jenkins-to-SonarQube connectivity
- SonarQube background tasks
- available memory on the EC2 instance

---

## Docker Invalid Tag Error

An error such as:

```text
docker build -t :25 .
```

means the image name variable is empty.

The image must contain a valid repository name, for example:

```text
motupallipavan/digital-banking-cicd-app:25
```

---

## Docker Hub Repository Error

Do not use:

```text
YOUR_DOCKER_USERNAME/digital-banking-cicd-app:25
```

Replace the placeholder with the actual Docker Hub username.

Example:

```text
motupallipavan/digital-banking-cicd-app:25
```

---

## Existing Containers

Before deployment:

```bash
docker compose down --remove-orphans
```

Then:

```bash
IMAGE_TAG=${BUILD_NUMBER} docker compose up -d
```

This prevents conflicts with the previous deployment.

---

# Security Considerations

This project demonstrates several DevOps security practices:

- SonarQube code-quality analysis
- Jenkins credential management
- Docker image versioning
- Container health checks
- Isolated Docker Compose networking
- PostgreSQL running as a separate service
- Automated Quality Gate validation before deployment

For a production implementation, database passwords, JWT secrets, and other sensitive values should be stored using a secure secrets-management solution rather than committed to source control.

---

# Key DevOps Concepts Demonstrated

This project demonstrates practical experience with:

- CI/CD pipeline automation
- Jenkins Declarative Pipeline
- Git-based source control
- Maven build automation
- Automated testing
- SonarQube code analysis
- Quality Gate enforcement
- Docker image creation
- Docker image versioning
- Docker Hub registry
- Docker Compose
- PostgreSQL containerization
- Automated deployment
- Container health checks
- Linux troubleshooting
- CI/CD failure troubleshooting

---

# Project Outcome

The completed pipeline provides an automated path from source code to a running Digital Banking application:

```text
Developer
   |
   v
GitHub
   |
   v
Jenkins
   |
   +--> Maven Compile
   |
   +--> Automated Tests
   |
   +--> SonarQube
   |
   +--> Quality Gate
   |
   +--> Maven Package
   |
   +--> Docker Build
   |
   +--> Docker Hub
   |
   +--> Docker Compose
   |
   +--> PostgreSQL
   |
   +--> Health Check
   |
   v
Running Digital Banking Application
```

## Author
**Pavan Motupalli
