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

## Testing

Run all tests:
```bash
mvn test
```

Run with coverage:
```bash
mvn test jacoco:report
```
## Added SonarQube

- **Sonarcube for code quality

## Doker Tag

- **For rolback 

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

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is for educational purposes.

