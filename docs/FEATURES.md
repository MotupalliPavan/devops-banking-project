# Digital Banking System - Feature Documentation

## Overview

This document provides a comprehensive breakdown of all features in the Digital Banking System, including user stories, acceptance criteria, and implementation details.

---

## Table of Contents

1. [Authentication & Security](#1-authentication--security)
2. [User Management](#2-user-management)
3. [Account Management](#3-account-management)
4. [Transaction Management](#4-transaction-management)
5. [Admin Dashboard](#5-admin-dashboard)
6. [Reporting & Statements](#6-reporting--statements)
7. [Notifications](#7-notifications)
8. [Audit & Logging](#8-audit--logging)

---

## 1. Authentication & Security

### 1.1 User Registration

**Feature ID:** AUTH-001

**Description:** Allow new users to create an account in the banking system.

**User Story:**
> As a new customer, I want to register for a banking account so that I can access digital banking services.

**Acceptance Criteria:**
- [x] User can register with email, password, first name, last name, and phone
- [x] Email must be unique in the system
- [x] Password must meet security requirements (min 8 chars)
- [x] User receives confirmation after successful registration
- [x] System prevents duplicate registrations
- [x] User is automatically assigned CUSTOMER role

**Business Rules:**
| Rule ID | Rule Description |
|---------|------------------|
| BR-001 | Email must be unique across all users |
| BR-002 | Password must be at least 8 characters with 1 uppercase, 1 number, 1 special character |
| BR-003 | Phone number must be valid Indian format (+91XXXXXXXXXX) |
| BR-004 | New users are assigned ACTIVE status by default |

**Implementation Notes:**
```
Controller: AuthController.register()
Service: AuthService.registerUser()
Repository: UserRepository.save()
Validation: CreateUserRequest DTO with Bean Validation
```

---

### 1.2 User Login

**Feature ID:** AUTH-002

**Description:** Allow registered users to authenticate and access their accounts.

**User Story:**
> As a registered user, I want to log in to my account so that I can access my banking services securely.

**Acceptance Criteria:**
- [x] User can log in with email and password
- [x] System returns JWT access token and refresh token on success
- [x] System returns appropriate error for invalid credentials
- [x] Suspended/Inactive users cannot log in
- [x] Failed login attempts are logged

**Business Rules:**
| Rule ID | Rule Description |
|---------|------------------|
| BR-005 | Only ACTIVE users can log in |
| BR-006 | Access token expires in 1 hour |
| BR-007 | Refresh token expires in 7 days |
| BR-008 | Maximum 5 failed login attempts before temporary lockout |

**Security Flow:**
```
1. User submits email + password
2. System validates credentials against database
3. System checks user status (must be ACTIVE)
4. System generates JWT access token + refresh token
5. System returns tokens to user
6. User includes access token in subsequent requests
```

---

### 1.3 Token Refresh

**Feature ID:** AUTH-003

**Description:** Allow users to refresh their access token without re-authenticating.

**User Story:**
> As a logged-in user, I want my session to remain active so that I don't have to log in repeatedly.

**Acceptance Criteria:**
- [x] User can exchange valid refresh token for new access token
- [x] Old refresh token is invalidated after use
- [x] Invalid/expired refresh tokens are rejected
- [x] New refresh token is issued with the new access token

---

### 1.4 Password Change

**Feature ID:** AUTH-004

**Description:** Allow users to change their password.

**User Story:**
> As a user, I want to change my password so that I can maintain account security.

**Acceptance Criteria:**
- [x] User must provide current password
- [x] New password must meet security requirements
- [x] New password must be different from current password
- [x] All active sessions are invalidated after password change
- [x] User is notified of password change

---

### 1.5 Logout

**Feature ID:** AUTH-005

**Description:** Allow users to securely end their session.

**User Story:**
> As a logged-in user, I want to log out so that my session is terminated securely.

**Acceptance Criteria:**
- [x] User's current token is invalidated
- [x] User cannot use the invalidated token for further requests
- [x] System returns success confirmation

---

## 2. User Management

### 2.1 View Profile

**Feature ID:** USER-001

**Description:** Allow users to view their profile information.

**User Story:**
> As a user, I want to view my profile so that I can see my account information.

**Acceptance Criteria:**
- [x] User can view their personal information (name, email, phone)
- [x] User can see their account summary (number of accounts, total balance)
- [x] User can see their account status and registration date
- [x] Sensitive information (password) is never displayed

**UI Mockup - Profile Page:**
```
┌─────────────────────────────────────────────────────────┐
│                    MY PROFILE                            │
├─────────────────────────────────────────────────────────┤
│  Name:        John Doe                                   │
│  Email:       john.doe@example.com                       │
│  Phone:       +91 98765 43210                            │
│  Status:      ● Active                                   │
│  Member Since: January 15, 2025                          │
├─────────────────────────────────────────────────────────┤
│                  ACCOUNT SUMMARY                         │
│  Total Accounts: 2                                       │
│  Total Balance:  ₹1,50,000.00                           │
├─────────────────────────────────────────────────────────┤
│  [Edit Profile]              [Change Password]           │
└─────────────────────────────────────────────────────────┘
```

---

### 2.2 Update Profile

**Feature ID:** USER-002

**Description:** Allow users to update their profile information.

**User Story:**
> As a user, I want to update my profile so that my information stays current.

**Acceptance Criteria:**
- [x] User can update first name, last name, and phone number
- [x] Email cannot be changed (contact support for email change)
- [x] Changes are saved immediately
- [x] User receives confirmation of successful update
- [x] Audit log is created for profile changes

---

## 3. Account Management

### 3.1 Create Bank Account

**Feature ID:** ACC-001

**Description:** Allow users to create new bank accounts.

**User Story:**
> As a user, I want to create a new bank account so that I can start banking.

**Acceptance Criteria:**
- [x] User can create SAVINGS or CURRENT account
- [x] User can make an optional initial deposit
- [x] System generates unique 16-digit account number
- [x] Account is created with ACTIVE status
- [x] User receives account creation confirmation with details

**Business Rules:**
| Rule ID | Rule Description |
|---------|------------------|
| BR-010 | Account number is system-generated (10 digits) |
| BR-011 | Initial deposit minimum: ₹0 (no minimum) |
| BR-012 | User can have maximum 5 accounts |
| BR-013 | New accounts are ACTIVE by default |

**Account Number Generation:**
```
Format: YYYYNNNNNN
- YYYY: Current year (e.g., 2025)
- NNNNNN: Sequential 6-digit number (e.g., 000001)
Example: 2025000001
```

---

### 3.2 View Account List

**Feature ID:** ACC-002

**Description:** Allow users to view all their bank accounts.

**User Story:**
> As a user, I want to see all my bank accounts so that I can manage them.

**Acceptance Criteria:**
- [x] User sees list of all their accounts
- [x] Each account shows: account number, type, balance, status
- [x] User can filter accounts by status or type
- [x] Accounts are sorted by creation date (newest first)

**UI Mockup - Account List:**
```
┌─────────────────────────────────────────────────────────────────┐
│                       MY ACCOUNTS                                │
├─────────────────────────────────────────────────────────────────┤
│  Filter: [All Types ▼]  [All Status ▼]      [+ New Account]     │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ 🏦 SAVINGS ACCOUNT              Account #: 2025000001   │    │
│  │    Balance: ₹50,000.00          Status: ● Active        │    │
│  │    [View Details]  [Transfer]  [Statement]              │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ 🏦 CURRENT ACCOUNT              Account #: 2025000002   │    │
│  │    Balance: ₹1,00,000.00        Status: ● Active        │    │
│  │    [View Details]  [Transfer]  [Statement]              │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

---

### 3.3 View Account Details

**Feature ID:** ACC-003

**Description:** Allow users to view detailed information about a specific account.

**User Story:**
> As a user, I want to view my account details so that I can see complete account information.

**Acceptance Criteria:**
- [x] User can view account number, type, balance, status
- [x] User can see account creation and last update dates
- [x] User can view recent transactions (last 5)
- [x] User can navigate to full transaction history

---

### 3.4 Check Account Balance

**Feature ID:** ACC-004

**Description:** Allow users to check their current account balance.

**User Story:**
> As a user, I want to check my account balance quickly so that I know my available funds.

**Acceptance Criteria:**
- [x] Balance is displayed in real-time
- [x] Balance shows currency (INR)
- [x] Balance timestamp is shown
- [x] User can only view balance of their own accounts

---

## 4. Transaction Management

### 4.1 Deposit Money

**Feature ID:** TXN-001

**Description:** Allow users to deposit money into their accounts.

**User Story:**
> As a user, I want to deposit money into my account so that I can increase my balance.

**Acceptance Criteria:**
- [x] User can deposit any positive amount
- [x] Account balance is updated immediately
- [x] Transaction record is created with COMPLETED status
- [x] User receives deposit confirmation with reference ID
- [x] Audit log is created

**Business Rules:**
| Rule ID | Rule Description |
|---------|------------------|
| BR-020 | Minimum deposit: ₹1.00 |
| BR-021 | Maximum deposit per transaction: ₹10,00,000 |
| BR-022 | Account must be ACTIVE to receive deposits |
| BR-023 | Transaction reference ID format: TXNYYYYMMDDHHmmssNNN |

**Transaction Flow:**
```
1. User initiates deposit request
2. System validates:
   - Account exists
   - Account is ACTIVE
   - Amount is valid (positive, within limits)
3. System executes deposit:
   - Update account balance
   - Create transaction record
   - Create audit log
4. System returns success with transaction details
```

---

### 4.2 Withdraw Money

**Feature ID:** TXN-002

**Description:** Allow users to withdraw money from their accounts.

**User Story:**
> As a user, I want to withdraw money from my account so that I can access my funds.

**Acceptance Criteria:**
- [x] User can withdraw amount up to available balance
- [x] Account balance is updated immediately
- [x] Transaction record is created with COMPLETED status
- [x] User receives withdrawal confirmation with reference ID
- [x] Insufficient balance shows appropriate error

**Business Rules:**
| Rule ID | Rule Description |
|---------|------------------|
| BR-024 | Minimum withdrawal: ₹100.00 |
| BR-025 | Maximum withdrawal per transaction: ₹50,000 |
| BR-026 | Balance cannot go below ₹0 |
| BR-027 | Account must be ACTIVE to allow withdrawals |

---

### 4.3 Fund Transfer

**Feature ID:** TXN-003

**Description:** Allow users to transfer money between accounts.

**User Story:**
> As a user, I want to transfer money to another account so that I can make payments.

**Acceptance Criteria:**
- [x] User can transfer to any valid account in the system
- [x] User can transfer from their own accounts only
- [x] Source account balance is debited
- [x] Destination account balance is credited
- [x] Single atomic transaction (both succeed or both fail)
- [x] User receives transfer confirmation

**Business Rules:**
| Rule ID | Rule Description |
|---------|------------------|
| BR-028 | Source and destination accounts must be different |
| BR-029 | Source account must have sufficient balance |
| BR-030 | Both accounts must be ACTIVE |
| BR-031 | Minimum transfer: ₹1.00 |
| BR-032 | Maximum transfer per transaction: ₹5,00,000 |
| BR-033 | Daily transfer limit: ₹10,00,000 |

**Transfer Flow:**
```
┌──────────────────────────────────────────────────────────────┐
│                     FUND TRANSFER                             │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│  1. VALIDATION                                                │
│     • Source account exists and belongs to user              │
│     • Destination account exists                              │
│     • Both accounts are ACTIVE                                │
│     • Sufficient balance in source                            │
│     • Amount within limits                                    │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│  2. EXECUTION (Atomic Transaction)                            │
│     • Lock source account                                     │
│     • Lock destination account                                │
│     • Debit source account                                    │
│     • Credit destination account                              │
│     • Create transaction record                               │
│     • Commit transaction                                      │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│  3. CONFIRMATION                                              │
│     • Return transaction reference ID                         │
│     • Show updated balances                                   │
│     • Create audit log                                        │
└──────────────────────────────────────────────────────────────┘
```

**UI Mockup - Transfer Form:**
```
┌─────────────────────────────────────────────────────────────┐
│                    FUND TRANSFER                             │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  From Account:  [2025000001 - Savings (₹50,000) ▼]          │
│                                                              │
│  To Account:    [________________________]                   │
│                 Enter account number                         │
│                                                              │
│  Amount (₹):    [________________________]                   │
│                 Min: ₹1 | Max: ₹5,00,000                    │
│                                                              │
│  Description:   [________________________]                   │
│                 (Optional)                                   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │ Available Balance: ₹50,000.00                       │    │
│  │ Transfer Fee: ₹0.00                                 │    │
│  │ Amount to Debit: ₹10,000.00                        │    │
│  └─────────────────────────────────────────────────────┘    │
│                                                              │
│           [Cancel]              [Transfer Now]               │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

### 4.4 View Transaction History

**Feature ID:** TXN-004

**Description:** Allow users to view their transaction history.

**User Story:**
> As a user, I want to view my transaction history so that I can track my financial activities.

**Acceptance Criteria:**
- [x] User can view all transactions for their accounts
- [x] Transactions show: date, type, amount, status, reference ID
- [x] User can filter by account, type, date range
- [x] User can sort by date, amount
- [x] Pagination for large transaction lists
- [x] User can search by reference ID

**Filter Options:**
| Filter | Values |
|--------|--------|
| Account | All accounts, or specific account |
| Type | All, Deposit, Withdrawal, Transfer |
| Status | All, Completed, Pending, Failed |
| Date Range | Custom start and end dates |

**UI Mockup - Transaction History:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                    TRANSACTION HISTORY                               │
├─────────────────────────────────────────────────────────────────────┤
│  Account: [All Accounts ▼]  Type: [All ▼]  Status: [All ▼]          │
│  From: [2025-01-01]  To: [2025-01-31]  [Search]                     │
├─────────────────────────────────────────────────────────────────────┤
│  Date         │ Type      │ Description      │ Amount    │ Balance  │
├───────────────┼───────────┼──────────────────┼───────────┼──────────┤
│  15 Jan 2025  │ TRANSFER  │ Payment to Jane  │ -₹10,000  │ ₹40,000 │
│  14 Jan 2025  │ DEPOSIT   │ Cash deposit     │ +₹25,000  │ ₹50,000 │
│  10 Jan 2025  │ WITHDRAW  │ ATM withdrawal   │ -₹5,000   │ ₹25,000 │
│  05 Jan 2025  │ DEPOSIT   │ Initial deposit  │ +₹30,000  │ ₹30,000 │
├─────────────────────────────────────────────────────────────────────┤
│  Showing 1-4 of 4 transactions    [◀ Previous]  [Next ▶]           │
└─────────────────────────────────────────────────────────────────────┘
```

---

### 4.5 View Transaction Details

**Feature ID:** TXN-005

**Description:** Allow users to view detailed information about a specific transaction.

**User Story:**
> As a user, I want to view transaction details so that I can see complete information about a specific transaction.

**Acceptance Criteria:**
- [x] User can view full transaction details by reference ID
- [x] Shows: reference ID, type, amount, accounts involved, status
- [x] Shows transaction timestamp
- [x] Shows balance after transaction
- [x] User can only view transactions for their own accounts

---

## 5. Admin Dashboard

### 5.1 View All Users

**Feature ID:** ADMIN-001

**Description:** Allow administrators to view and manage all users.

**User Story:**
> As an admin, I want to view all users so that I can manage the customer base.

**Acceptance Criteria:**
- [x] Admin can view list of all users
- [x] Shows: name, email, status, account count, total balance
- [x] Admin can search by name or email
- [x] Admin can filter by status
- [x] Pagination for large user lists

---

### 5.2 Manage User Status

**Feature ID:** ADMIN-002

**Description:** Allow administrators to activate, deactivate, or suspend users.

**User Story:**
> As an admin, I want to manage user status so that I can control account access.

**Acceptance Criteria:**
- [x] Admin can change user status to ACTIVE, INACTIVE, or SUSPENDED
- [x] Admin must provide reason for status change
- [x] Status change is logged in audit trail
- [x] User is notified of status change (optional)
- [x] Suspended users cannot log in

**Status Transitions:**
```
ACTIVE ──────► INACTIVE (Admin deactivates)
   │              │
   │              ▼
   │          SUSPENDED (Admin suspends)
   │              │
   ▼              │
INACTIVE ◄───────┘ (Admin reactivates)
```

---

### 5.3 Manage Account Status

**Feature ID:** ADMIN-003

**Description:** Allow administrators to freeze or unfreeze accounts.

**User Story:**
> As an admin, I want to manage account status so that I can control account operations.

**Acceptance Criteria:**
- [x] Admin can change account status to ACTIVE, INACTIVE, or FROZEN
- [x] Admin must provide reason for status change
- [x] Frozen accounts cannot perform any transactions
- [x] Status change is logged in audit trail

---

### 5.4 Dashboard Statistics

**Feature ID:** ADMIN-004

**Description:** Provide administrators with system-wide statistics.

**User Story:**
> As an admin, I want to see dashboard statistics so that I can monitor system health.

**Acceptance Criteria:**
- [x] Shows total users (by status)
- [x] Shows total accounts (by type)
- [x] Shows transaction volume (today, this month)
- [x] Shows recent activity summary
- [x] Data refreshes periodically

**UI Mockup - Admin Dashboard:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                       ADMIN DASHBOARD                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐               │
│  │   USERS      │  │  ACCOUNTS    │  │ TRANSACTIONS │               │
│  │   1,500      │  │   2,000      │  │   150 today  │               │
│  │  +50 this    │  │  Savings:    │  │  ₹5L volume  │               │
│  │   month      │  │  1,500       │  │              │               │
│  └──────────────┘  └──────────────┘  └──────────────┘               │
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │ RECENT TRANSACTIONS                                         │    │
│  ├─────────────────────────────────────────────────────────────┤    │
│  │ 10:30 AM │ John Doe    │ Transfer │ ₹10,000 │ Completed     │    │
│  │ 10:25 AM │ Jane Smith  │ Deposit  │ ₹25,000 │ Completed     │    │
│  │ 10:20 AM │ Bob Wilson  │ Withdraw │ ₹5,000  │ Completed     │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  [View All Users]  [View All Accounts]  [View All Transactions]     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 6. Reporting & Statements

### 6.1 Account Statement

**Feature ID:** RPT-001

**Description:** Generate account statements for a specified period.

**User Story:**
> As a user, I want to generate an account statement so that I can have a record of my transactions.

**Acceptance Criteria:**
- [x] User can select date range (start and end date)
- [x] Statement shows opening and closing balance
- [x] Statement lists all transactions in the period
- [x] Shows total credits and debits
- [x] Available in JSON format (PDF optional)

**Statement Format:**
```
┌─────────────────────────────────────────────────────────────────────┐
│                      ACCOUNT STATEMENT                               │
├─────────────────────────────────────────────────────────────────────┤
│  Account Number: 2025000001                                          │
│  Account Type:   Savings Account                                     │
│  Account Holder: John Doe                                            │
│  Statement Period: 01 Jan 2025 - 31 Jan 2025                        │
├─────────────────────────────────────────────────────────────────────┤
│  Opening Balance (01 Jan 2025):      ₹45,000.00                     │
├─────────────────────────────────────────────────────────────────────┤
│  Date       │ Reference       │ Description    │ Debit   │ Credit   │
├─────────────┼─────────────────┼────────────────┼─────────┼──────────┤
│  05 Jan     │ TXN202501050001 │ Cash deposit   │    -    │ 10,000   │
│  10 Jan     │ TXN202501100002 │ Bill payment   │ 5,000   │    -     │
│  15 Jan     │ TXN202501150003 │ Transfer rcvd  │    -    │ 15,000   │
│  20 Jan     │ TXN202501200004 │ ATM withdrawal │ 5,000   │    -     │
├─────────────────────────────────────────────────────────────────────┤
│  Total Debits:  ₹10,000.00          Total Credits: ₹25,000.00       │
├─────────────────────────────────────────────────────────────────────┤
│  Closing Balance (31 Jan 2025):      ₹60,000.00                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 7. Notifications

### 7.1 Transaction Alerts

**Feature ID:** NOTIF-001

**Description:** Notify users of transaction activities (future enhancement).

**User Story:**
> As a user, I want to receive transaction alerts so that I'm aware of account activity.

**Planned Notifications:**
- Deposit confirmation
- Withdrawal confirmation
- Transfer sent/received
- Low balance warning
- Login from new device

**Note:** For the initial version, notifications will be shown in-app only. Email/SMS notifications are a future enhancement.

---

## 8. Audit & Logging

### 8.1 Audit Trail

**Feature ID:** AUDIT-001

**Description:** Maintain comprehensive audit logs for all sensitive operations.

**User Story:**
> As a system, I need to maintain audit logs so that all actions are traceable.

**Logged Events:**
| Event Type | Details Logged |
|------------|----------------|
| User Registration | User ID, email, timestamp |
| Login/Logout | User ID, IP address, timestamp, success/failure |
| Profile Update | User ID, fields changed, old/new values |
| Account Creation | Account ID, user ID, type |
| Transaction | Reference ID, type, amount, accounts, status |
| Status Change | Entity type, old/new status, admin ID, reason |

**Audit Log Schema:**
```
{
  "id": 1,
  "userId": 1,
  "action": "TRANSFER",
  "entityType": "TRANSACTION",
  "entityId": 123,
  "oldValue": null,
  "newValue": "{amount: 10000, to: '2025000002'}",
  "ipAddress": "192.168.1.1",
  "createdAt": "2025-01-15T12:00:00Z"
}
```

---

## Feature Priority Matrix

| Priority | Feature ID | Feature Name | Complexity |
|----------|------------|--------------|------------|
| P0 (Must Have) | AUTH-001 | User Registration | Medium |
| P0 (Must Have) | AUTH-002 | User Login | Medium |
| P0 (Must Have) | ACC-001 | Create Account | Low |
| P0 (Must Have) | ACC-002 | View Accounts | Low |
| P0 (Must Have) | TXN-001 | Deposit | Medium |
| P0 (Must Have) | TXN-002 | Withdrawal | Medium |
| P0 (Must Have) | TXN-003 | Fund Transfer | High |
| P0 (Must Have) | TXN-004 | Transaction History | Medium |
| P1 (Should Have) | AUTH-003 | Token Refresh | Low |
| P1 (Should Have) | AUTH-004 | Password Change | Low |
| P1 (Should Have) | USER-002 | Update Profile | Low |
| P1 (Should Have) | RPT-001 | Account Statement | Medium |
| P1 (Should Have) | ADMIN-001 | View All Users | Low |
| P1 (Should Have) | ADMIN-004 | Dashboard Stats | Medium |
| P2 (Nice to Have) | ADMIN-002 | Manage User Status | Low |
| P2 (Nice to Have) | ADMIN-003 | Manage Account Status | Low |
| P2 (Nice to Have) | AUDIT-001 | Audit Trail | Medium |

---

## Implementation Phases

### Phase 1: Core Banking (MVP)
- User Registration & Login
- Account Creation & Viewing
- Deposit, Withdrawal, Transfer
- Transaction History

### Phase 2: Enhanced Features
- Profile Management
- Password Change
- Account Statements
- Basic Admin Dashboard

### Phase 3: Administration
- User Management
- Account Status Management
- Advanced Dashboard Statistics
- Audit Logging

---

## Non-Functional Requirements

| Requirement | Target | Notes |
|-------------|--------|-------|
| Page Load Time | < 2 seconds | For all pages |
| API Response Time | < 200ms (p95) | For all endpoints |
| Availability | 99.9% | Monthly uptime |
| Concurrent Users | 100 | Simultaneous active users |
| Transaction Throughput | 50 TPS | Transactions per second |
| Data Retention | 7 years | Transaction data |
