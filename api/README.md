# MyFinance API - Backend

Spring Boot REST API with AI-powered financial assistant using LangChain4j.

## Quick Start

### 1. Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL with pgvector (via Docker Compose from root directory)

### 2. Start Database
From the root directory:
```bash
cd ..
docker-compose up -d
```

### 3. Configure Environment
Create `src/main/resources/application-local.yml`:
```yaml
spring:
  datasource:
    password: postgres

langchain4j:
  open-ai:
    chat-model:
      api-key: your-openai-api-key-here
    embedding-model:
      api-key: your-openai-api-key-here
```

### 4. Run Application
```bash
mvn clean install
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

## Available Endpoints

### User Management
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID

### Transactions
- `GET /api/transactions` - List all transactions
- `POST /api/transactions` - Create transaction
- `PUT /api/transactions/{id}` - Update transaction
- `DELETE /api/transactions/{id}` - Delete transaction

### Categories
- `GET /api/categories` - List all categories
- `POST /api/categories` - Create category

### Bank Accounts
- `GET /api/bank-accounts` - List all accounts
- `POST /api/bank-accounts` - Create account

### Balance
- `GET /api/balance/total` - Get total balance
- `GET /api/balance/by-bank-account` - Balance by account
- `GET /api/balance/by-category` - Balance by category

### AI Assistant
- `POST /api/assistant/chat` - Send message to AI assistant
- `GET /api/assistant/history/{userId}` - Get chat history

## AI Tools

The AI assistant has access to 11 tools for natural language interaction:

### Transaction Management
- `createTransaction` - Create new transaction with optional date
- `updateTransaction` - Update existing transaction
- `deleteTransaction` - Delete transaction
- `getTransactionHistory` - List all transactions

### Category Management
- `listCategories` - List all categories
- `createCategory` - Create new category

### Bank Account Management
- `listBankAccounts` - List all accounts
- `createBankAccount` - Create new account

### Balance Queries
- `getTotalBalance` - Get total balance summary
- `getBalanceByBankAccount` - Balance breakdown by account
- `getBalanceByCategory` - Balance breakdown by category

See [TOOL_CALLING_GUIDE.md](TOOL_CALLING_GUIDE.md) for detailed documentation.

## Development

### Run Tests
```bash
mvn test
```

### Compile Only
```bash
mvn clean compile -DskipTests
```

### Package Application
```bash
mvn clean package
```

### Run with Different Profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:
- `V1__create_tables.sql` - Initial schema
- `V2__add_user_id_to_categories.sql` - User categories
- `V3__create_messages_table.sql` - Chat messages
- `V4__seed_initial_data.sql` - Sample data
- `V5__add_description_to_transactions.sql` - Transaction descriptions
- `V6__add_created_at_to_transactions.sql` - Transaction dates

## Technology Stack
- Spring Boot 3.5.3
- Java 17
- LangChain4j 1.0.0-beta5
- PostgreSQL + pgvector
- Flyway (database migrations)
- Lombok (code generation)

## Architecture
```
src/main/java/com/myfinance/
├── ai/                    # AI tools for LangChain4j
│   ├── FinanceAssistant.java
│   ├── TransactionTools.java
│   └── BalanceTools.java
├── config/               # Spring configuration
│   ├── ChatMemoryConfig.java
│   └── PgVectorConfig.java
├── controller/          # REST controllers
│   └── dto/            # Data Transfer Objects
├── model/              # JPA entities
├── repository/         # Spring Data repositories
└── service/           # Business logic
```

## Debugging
Enable DEBUG logging for AI tools by checking `application.yml`:
```yaml
logging:
  level:
    com.myfinance.ai: DEBUG
```

This will show in console:
```
[TOOL] createTransaction called - type: DEBIT, categoryName: Food, ...
[TOOL] getTotalBalance called
```
