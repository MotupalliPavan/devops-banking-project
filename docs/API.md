# Digital Banking System - API Documentation

## Overview

This document provides comprehensive REST API documentation for the Digital Banking System. All APIs follow RESTful conventions and return JSON responses.

**Base URL:** `/api/v1`

**Authentication:** JWT Bearer Token (except public endpoints)

---

## Table of Contents

1. [Authentication APIs](#1-authentication-apis)
2. [User Management APIs](#2-user-management-apis)
3. [Account Management APIs](#3-account-management-apis)
4. [Transaction APIs](#4-transaction-apis)
5. [Admin APIs](#5-admin-apis)
6. [Error Responses](#6-error-responses)
7. [Pagination](#7-pagination)

---

## 1. Authentication APIs

### 1.1 Register User

Creates a new user account.

**Endpoint:** `POST /api/v1/auth/register`

**Access:** Public

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+919876543210"
}
```

**Request Validation:**
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| email | String | Yes | Valid email format, unique |
| password | String | Yes | Min 8 chars, 1 uppercase, 1 number, 1 special |
| firstName | String | Yes | 2-100 characters |
| lastName | String | Yes | 2-100 characters |
| phone | String | No | Valid phone format |

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210",
    "role": "CUSTOMER",
    "status": "ACTIVE",
    "createdAt": "2025-01-15T10:30:00Z"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": {
      "email": "Email already exists",
      "password": "Password must be at least 8 characters"
    }
  }
}
```

---

### 1.2 Login

Authenticates user and returns JWT token.

**Endpoint:** `POST /api/v1/auth/login`

**Access:** Public

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "CUSTOMER"
    }
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Invalid email or password"
  }
}
```

---

### 1.3 Refresh Token

Refreshes the access token using refresh token.

**Endpoint:** `POST /api/v1/auth/refresh`

**Access:** Public

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

---

### 1.4 Logout

Invalidates the current session/token.

**Endpoint:** `POST /api/v1/auth/logout`

**Access:** Authenticated

**Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

### 1.5 Change Password

Changes the user's password.

**Endpoint:** `POST /api/v1/auth/change-password`

**Access:** Authenticated

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewSecurePass456!",
  "confirmPassword": "NewSecurePass456!"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

---

## 2. User Management APIs

### 2.1 Get Current User Profile

Retrieves the authenticated user's profile.

**Endpoint:** `GET /api/v1/users/me`

**Access:** Authenticated

**Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210",
    "role": "CUSTOMER",
    "status": "ACTIVE",
    "accounts": [
      {
        "id": 1,
        "accountNumber": "1234567890",
        "accountType": "SAVINGS",
        "balance": 50000.00,
        "status": "ACTIVE"
      }
    ],
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-15T10:30:00Z"
  }
}
```

---

### 2.2 Update User Profile

Updates the user's profile information.

**Endpoint:** `PUT /api/v1/users/me`

**Access:** Authenticated

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe Updated",
  "phone": "+919876543211"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe Updated",
    "phone": "+919876543211",
    "role": "CUSTOMER",
    "status": "ACTIVE",
    "updatedAt": "2025-01-15T12:00:00Z"
  }
}
```

---

## 3. Account Management APIs

### 3.1 Create Account

Creates a new bank account for the authenticated user.

**Endpoint:** `POST /api/v1/accounts`

**Access:** Authenticated

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "accountType": "SAVINGS",
  "initialDeposit": 1000.00
}
```

**Request Validation:**
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| accountType | String | Yes | SAVINGS or CURRENT |
| initialDeposit | Decimal | No | Min 0, default 0 |

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Account created successfully",
  "data": {
    "id": 1,
    "accountNumber": "1234567890",
    "accountType": "SAVINGS",
    "balance": 1000.00,
    "status": "ACTIVE",
    "userId": 1,
    "createdAt": "2025-01-15T10:30:00Z"
  }
}
```

---

### 3.2 Get All Accounts

Retrieves all accounts for the authenticated user.

**Endpoint:** `GET /api/v1/accounts`

**Access:** Authenticated

**Headers:**
```
Authorization: Bearer <access_token>
```

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | String | No | Filter by status (ACTIVE, INACTIVE, FROZEN) |
| type | String | No | Filter by type (SAVINGS, CURRENT) |

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "accountNumber": "1234567890",
      "accountType": "SAVINGS",
      "balance": 50000.00,
      "status": "ACTIVE",
      "createdAt": "2025-01-15T10:30:00Z"
    },
    {
      "id": 2,
      "accountNumber": "1234567891",
      "accountType": "CURRENT",
      "balance": 100000.00,
      "status": "ACTIVE",
      "createdAt": "2025-01-16T09:00:00Z"
    }
  ]
}
```

---

### 3.3 Get Account by Account Number

Retrieves a specific account by account number.

**Endpoint:** `GET /api/v1/accounts/{accountNumber}`

**Access:** Authenticated (owner only)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| accountNumber | String | The account number |

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "accountNumber": "1234567890",
    "accountType": "SAVINGS",
    "balance": 50000.00,
    "status": "ACTIVE",
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe"
    },
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-15T10:30:00Z"
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "success": false,
  "error": {
    "code": "NOT_FOUND",
    "message": "Account not found with account number: 1234567890"
  }
}
```

---

### 3.4 Get Account Balance

Retrieves the current balance for an account.

**Endpoint:** `GET /api/v1/accounts/{accountNumber}/balance`

**Access:** Authenticated (owner only)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "accountNumber": "1234567890",
    "balance": 50000.00,
    "currency": "INR",
    "asOf": "2025-01-15T12:00:00Z"
  }
}
```

---

## 4. Transaction APIs

### 4.1 Deposit Money

Deposits money into an account.

**Endpoint:** `POST /api/v1/transactions/deposit`

**Access:** Authenticated

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "accountNumber": "1234567890",
  "amount": 5000.00,
  "description": "Cash deposit"
}
```

**Request Validation:**
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| accountNumber | String | Yes | Valid account number |
| amount | Decimal | Yes | Positive, max 1000000 per transaction |
| description | String | No | Max 500 characters |

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Deposit successful",
  "data": {
    "id": 1,
    "referenceId": "TXN20250115123456789",
    "type": "DEPOSIT",
    "amount": 5000.00,
    "toAccount": "1234567890",
    "status": "COMPLETED",
    "description": "Cash deposit",
    "balanceAfter": 55000.00,
    "createdAt": "2025-01-15T12:00:00Z"
  }
}
```

---

### 4.2 Withdraw Money

Withdraws money from an account.

**Endpoint:** `POST /api/v1/transactions/withdraw`

**Access:** Authenticated (owner only)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "accountNumber": "1234567890",
  "amount": 2000.00,
  "description": "ATM withdrawal"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Withdrawal successful",
  "data": {
    "id": 2,
    "referenceId": "TXN20250115123456790",
    "type": "WITHDRAWAL",
    "amount": 2000.00,
    "fromAccount": "1234567890",
    "status": "COMPLETED",
    "description": "ATM withdrawal",
    "balanceAfter": 53000.00,
    "createdAt": "2025-01-15T12:05:00Z"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "error": {
    "code": "INSUFFICIENT_BALANCE",
    "message": "Insufficient balance in account: 1234567890"
  }
}
```

---

### 4.3 Transfer Money

Transfers money between accounts.

**Endpoint:** `POST /api/v1/transactions/transfer`

**Access:** Authenticated (source account owner)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "fromAccountNumber": "1234567890",
  "toAccountNumber": "0987654321",
  "amount": 10000.00,
  "description": "Payment for services"
}
```

**Request Validation:**
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| fromAccountNumber | String | Yes | Valid account, owned by user |
| toAccountNumber | String | Yes | Valid account, different from source |
| amount | Decimal | Yes | Positive, <= source balance, max limit |
| description | String | No | Max 500 characters |

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Transfer successful",
  "data": {
    "id": 3,
    "referenceId": "TXN20250115123456791",
    "type": "TRANSFER",
    "amount": 10000.00,
    "fromAccount": "1234567890",
    "toAccount": "0987654321",
    "status": "COMPLETED",
    "description": "Payment for services",
    "balanceAfter": 43000.00,
    "createdAt": "2025-01-15T12:10:00Z"
  }
}
```

**Error Responses:**

*Insufficient Balance (400):*
```json
{
  "success": false,
  "error": {
    "code": "INSUFFICIENT_BALANCE",
    "message": "Insufficient balance in account: 1234567890"
  }
}
```

*Account Not Active (400):*
```json
{
  "success": false,
  "error": {
    "code": "ACCOUNT_NOT_ACTIVE",
    "message": "Account is not active: 0987654321"
  }
}
```

*Same Account Transfer (400):*
```json
{
  "success": false,
  "error": {
    "code": "INVALID_TRANSFER",
    "message": "Cannot transfer to the same account"
  }
}
```

---

### 4.4 Get Transaction History

Retrieves transaction history for an account.

**Endpoint:** `GET /api/v1/transactions`

**Access:** Authenticated

**Headers:**
```
Authorization: Bearer <access_token>
```

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| accountNumber | String | No | Filter by account number |
| type | String | No | Filter by type (DEPOSIT, WITHDRAWAL, TRANSFER) |
| status | String | No | Filter by status |
| startDate | Date | No | Start date (ISO 8601: YYYY-MM-DD) |
| endDate | Date | No | End date (ISO 8601: YYYY-MM-DD) |
| page | Integer | No | Page number (default: 0) |
| size | Integer | No | Page size (default: 20, max: 100) |
| sort | String | No | Sort field (default: createdAt,desc) |

**Example Request:**
```
GET /api/v1/transactions?accountNumber=1234567890&type=TRANSFER&startDate=2025-01-01&endDate=2025-01-31&page=0&size=10
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 3,
        "referenceId": "TXN20250115123456791",
        "type": "TRANSFER",
        "amount": 10000.00,
        "fromAccount": "1234567890",
        "toAccount": "0987654321",
        "status": "COMPLETED",
        "description": "Payment for services",
        "balanceAfter": 43000.00,
        "createdAt": "2025-01-15T12:10:00Z"
      },
      {
        "id": 2,
        "referenceId": "TXN20250115123456790",
        "type": "WITHDRAWAL",
        "amount": 2000.00,
        "fromAccount": "1234567890",
        "toAccount": null,
        "status": "COMPLETED",
        "description": "ATM withdrawal",
        "balanceAfter": 53000.00,
        "createdAt": "2025-01-15T12:05:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 10,
      "totalElements": 25,
      "totalPages": 3,
      "first": true,
      "last": false
    }
  }
}
```

---

### 4.5 Get Transaction by Reference ID

Retrieves a specific transaction by reference ID.

**Endpoint:** `GET /api/v1/transactions/{referenceId}`

**Access:** Authenticated (involved parties only)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 3,
    "referenceId": "TXN20250115123456791",
    "type": "TRANSFER",
    "amount": 10000.00,
    "fromAccount": {
      "accountNumber": "1234567890",
      "accountType": "SAVINGS",
      "holderName": "John Doe"
    },
    "toAccount": {
      "accountNumber": "0987654321",
      "accountType": "SAVINGS",
      "holderName": "Jane Smith"
    },
    "status": "COMPLETED",
    "description": "Payment for services",
    "balanceAfter": 43000.00,
    "createdAt": "2025-01-15T12:10:00Z"
  }
}
```

---

### 4.6 Get Account Statement

Generates an account statement for a date range.

**Endpoint:** `GET /api/v1/accounts/{accountNumber}/statement`

**Access:** Authenticated (owner only)

**Headers:**
```
Authorization: Bearer <access_token>
```

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| startDate | Date | Yes | Start date (ISO 8601) |
| endDate | Date | Yes | End date (ISO 8601) |
| format | String | No | Response format (json, pdf) - default: json |

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "accountNumber": "1234567890",
    "accountType": "SAVINGS",
    "holderName": "John Doe",
    "statementPeriod": {
      "from": "2025-01-01",
      "to": "2025-01-31"
    },
    "openingBalance": 45000.00,
    "closingBalance": 55000.00,
    "totalCredits": 15000.00,
    "totalDebits": 5000.00,
    "transactions": [
      {
        "date": "2025-01-05T10:00:00Z",
        "referenceId": "TXN20250105100000001",
        "description": "Cash deposit",
        "type": "CREDIT",
        "amount": 10000.00,
        "balance": 55000.00
      },
      {
        "date": "2025-01-10T14:30:00Z",
        "referenceId": "TXN20250110143000002",
        "description": "Bill payment",
        "type": "DEBIT",
        "amount": 5000.00,
        "balance": 50000.00
      }
    ],
    "generatedAt": "2025-01-15T12:00:00Z"
  }
}
```

---

## 5. Admin APIs

### 5.1 Get All Users

Retrieves all users (admin only).

**Endpoint:** `GET /api/v1/admin/users`

**Access:** Admin only

**Headers:**
```
Authorization: Bearer <access_token>
```

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | String | No | Filter by status |
| search | String | No | Search by name or email |
| page | Integer | No | Page number |
| size | Integer | No | Page size |

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "email": "john.doe@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "phone": "+919876543210",
        "role": "CUSTOMER",
        "status": "ACTIVE",
        "accountCount": 2,
        "totalBalance": 150000.00,
        "createdAt": "2025-01-15T10:30:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 100,
      "totalPages": 5
    }
  }
}
```

---

### 5.2 Get User by ID

Retrieves a specific user by ID (admin only).

**Endpoint:** `GET /api/v1/admin/users/{userId}`

**Access:** Admin only

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210",
    "role": "CUSTOMER",
    "status": "ACTIVE",
    "accounts": [
      {
        "id": 1,
        "accountNumber": "1234567890",
        "accountType": "SAVINGS",
        "balance": 50000.00,
        "status": "ACTIVE"
      }
    ],
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-15T10:30:00Z"
  }
}
```

---

### 5.3 Update User Status

Updates a user's status (admin only).

**Endpoint:** `PATCH /api/v1/admin/users/{userId}/status`

**Access:** Admin only

**Request Body:**
```json
{
  "status": "SUSPENDED",
  "reason": "Suspicious activity detected"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "User status updated successfully",
  "data": {
    "id": 1,
    "email": "john.doe@example.com",
    "status": "SUSPENDED",
    "updatedAt": "2025-01-15T14:00:00Z"
  }
}
```

---

### 5.4 Update Account Status

Updates an account's status (admin only).

**Endpoint:** `PATCH /api/v1/admin/accounts/{accountNumber}/status`

**Access:** Admin only

**Request Body:**
```json
{
  "status": "FROZEN",
  "reason": "Court order"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Account status updated successfully",
  "data": {
    "accountNumber": "1234567890",
    "status": "FROZEN",
    "updatedAt": "2025-01-15T14:00:00Z"
  }
}
```

---

### 5.5 Get Dashboard Statistics

Retrieves dashboard statistics (admin only).

**Endpoint:** `GET /api/v1/admin/dashboard/stats`

**Access:** Admin only

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "users": {
      "total": 1500,
      "active": 1400,
      "inactive": 80,
      "suspended": 20,
      "newThisMonth": 50
    },
    "accounts": {
      "total": 2000,
      "savings": 1500,
      "current": 500,
      "totalBalance": 50000000.00
    },
    "transactions": {
      "today": {
        "count": 150,
        "volume": 500000.00
      },
      "thisMonth": {
        "count": 3500,
        "volume": 15000000.00
      },
      "byType": {
        "deposits": 1200,
        "withdrawals": 800,
        "transfers": 1500
      }
    },
    "asOf": "2025-01-15T14:00:00Z"
  }
}
```

---

## 6. Error Responses

### 6.1 Standard Error Format

All error responses follow this format:

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": {},
    "timestamp": "2025-01-15T12:00:00Z",
    "path": "/api/v1/resource"
  }
}
```

### 6.2 Error Codes Reference

| HTTP Status | Error Code | Description |
|-------------|------------|-------------|
| 400 | VALIDATION_ERROR | Request validation failed |
| 400 | INSUFFICIENT_BALANCE | Not enough balance for transaction |
| 400 | ACCOUNT_NOT_ACTIVE | Account is frozen or inactive |
| 400 | INVALID_TRANSFER | Invalid transfer (same account, etc.) |
| 401 | UNAUTHORIZED | Authentication required |
| 401 | INVALID_CREDENTIALS | Wrong email or password |
| 401 | TOKEN_EXPIRED | JWT token has expired |
| 403 | FORBIDDEN | Access denied |
| 404 | NOT_FOUND | Resource not found |
| 409 | DUPLICATE_ENTRY | Resource already exists |
| 429 | RATE_LIMIT_EXCEEDED | Too many requests |
| 500 | INTERNAL_ERROR | Server error |

### 6.3 Validation Error Example

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": {
      "email": "Must be a valid email address",
      "password": "Must be at least 8 characters",
      "amount": "Must be positive"
    },
    "timestamp": "2025-01-15T12:00:00Z",
    "path": "/api/v1/auth/register"
  }
}
```

---

## 7. Pagination

### 7.1 Paginated Request

All list endpoints support pagination with these parameters:

| Parameter | Type | Default | Max | Description |
|-----------|------|---------|-----|-------------|
| page | Integer | 0 | - | Page number (0-indexed) |
| size | Integer | 20 | 100 | Items per page |
| sort | String | varies | - | Sort field and direction |

**Sort Format:** `field,direction` (e.g., `createdAt,desc`)

**Multiple Sort:** `sort=lastName,asc&sort=firstName,asc`

### 7.2 Paginated Response

```json
{
  "success": true,
  "data": {
    "content": [...],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 100,
      "totalPages": 5,
      "first": true,
      "last": false,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

---

## 8. Rate Limiting

API requests are rate-limited per user/IP:

| Endpoint Category | Limit | Window |
|-------------------|-------|--------|
| Authentication | 10 requests | 1 minute |
| Transactions | 30 requests | 1 minute |
| Read Operations | 100 requests | 1 minute |
| Admin Operations | 50 requests | 1 minute |

**Rate Limit Headers:**
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1705320000
```

**Rate Limit Exceeded Response (429):**
```json
{
  "success": false,
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Too many requests. Please try again later.",
    "retryAfter": 60
  }
}
```

---

## 9. API Versioning

The API uses URL path versioning:
- Current version: `v1`
- Base path: `/api/v1/`

Future versions will be available at `/api/v2/`, etc.

---

## 10. OpenAPI/Swagger

Interactive API documentation is available at:
- **Swagger UI:** `/swagger-ui.html`
- **OpenAPI Spec:** `/v3/api-docs`

---

## Appendix: HTTP Status Codes Summary

| Status | Meaning | Usage |
|--------|---------|-------|
| 200 | OK | Successful GET, PUT, PATCH, DELETE |
| 201 | Created | Successful POST (resource created) |
| 204 | No Content | Successful DELETE (no response body) |
| 400 | Bad Request | Validation error, business rule violation |
| 401 | Unauthorized | Authentication required or failed |
| 403 | Forbidden | Authenticated but not authorized |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate resource |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unexpected server error |
