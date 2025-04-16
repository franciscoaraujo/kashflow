# Wallet Service API

## Overview

This is a simple implementation of a digital wallet service, providing various functionalities like wallet creation, deposit, withdrawal, transfer, and transaction auditing. The service is designed using Spring Boot and integrates with PostgreSQL for persistent storage, Kafka for message handling, and Prometheus/Grafana for observability.

## Functional Requirements

1. **Wallet Creation**: Allows users to create a new wallet with an initial balance.
2. **Deposit**: Allows users to deposit funds into their wallets.
3. **Withdrawal**: Allows users to withdraw funds from their wallets.
4. **Transfer**: Enables users to transfer funds between wallets.
5. **Balance & Transaction History**: Provides functionalities to check wallet balance and transaction history.
6. **Transaction Auditing**: Keeps an audit trail of all transactions for security and monitoring purposes.

## Non-Functional Requirements

1. **Scalability**: Designed to scale horizontally to handle large volumes of transactions and requests.
2. **Resilience**: The service uses **Resilience4j** to implement retry and circuit breaker patterns to handle temporary failures and avoid service outages.
3. **Security**: Authentication and authorization can be easily integrated, although they are not implemented in the first version.
4. **Observability**: Integrated with **Spring Boot Actuator** and **Prometheus** for health checks and performance monitoring, with **Grafana** for visualization.
5. **High Availability**: The service can be deployed in a containerized environment using **Docker Compose**, ensuring high availability.

## Endpoints

### 1. Health Check
- **GET** `/kashflow/api/v1/wallet/check`
- **Description**: Verifies that the service is running.
- **Response**: `200 OK` with message: `API KASHFLOW HEALTH - OK`.

### 2. Create Wallet
- **POST** `/kashflow/api/v1/wallet/create`
- **Description**: Creates a new wallet with an initial balance.
- **Request Body**: `WalletRequestDto`
- **Response**: `200 OK` with the created wallet details.

### 3. Deposit Funds
- **POST** `/kashflow/api/v1/wallet/deposit`
- **Description**: Deposits funds into the specified wallet.
- **Request Body**: `WalletDepositDto`
- **Response**: `200 OK` with message: `Deposit successful`.

### 4. Withdraw Funds
- **POST** `/kashflow/api/v1/wallet/withdraw`
- **Description**: Withdraws funds from the specified wallet.
- **Request Body**: `WalletWithdrawDto`
- **Response**: `200 OK` with message: `Withdraw successful`.

### 5. Transfer Funds
- **POST** `/kashflow/api/v1/wallet/transfer`
- **Description**: Transfers funds between wallets.
- **Request Body**: `WalletTransferDto`
- **Response**: `200 OK` with message: `Transfer successful`.

### 6. Get Balance
- **GET** `/kashflow/api/v1/wallet/balance/{documentNumber}`
- **Description**: Retrieves the balance for a wallet based on the document number.
- **Path Variable**: `documentNumber` - Wallet identifier.
- **Request Parameters**: `page` (optional, default=0), `size` (optional, default=10)
- **Response**: `200 OK` with wallet balance details.

### 7. Get Transaction History Summary
- **GET** `/kashflow/api/v1/wallet/balance/resumeTransaction/documentNumber/{documentNumber}/dateTransaction/{dateTransaction}`
- **Description**: Retrieves the historical transaction balance summary for a specific wallet, based on document number and transaction date.
- **Path Variables**:
    - `documentNumber`: Wallet identifier.
    - `dateTransaction`: Transaction date (YYYY-MM-DD).
- **Request Parameters**: `page` (optional, default=0), `size` (optional, default=10)
- **Response**: `200 OK` with transaction summary.

### 8. Get Audit Logs by Wallet ID
- **GET** `/kashflow/api/v1/wallet/audit/{walletId}`
- **Description**: Retrieves all audit logs for a specific wallet.
- **Path Variable**: `walletId` - Wallet identifier.
- **Response**: `200 OK` with audit transaction details.

## Architecture

- **Backend Framework**: Spring Boot
- **Database**: PostgreSQL
- **Messaging**: Kafka (for event-driven architecture)
- **Monitoring**: Prometheus + Grafana (for observability)
- **Resilience**: Resilience4j (for retry and circuit breaker patterns)
- **Containerization**: Docker + Docker Compose (for deployment)

## Setup and Running the Application

### Prerequisites

- JDK 11+
- Maven
- Docker
- Docker Compose
- PostgreSQL Database (configured via Docker Compose)

### Running Locally

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-repository/your-project.git
   cd your-project
