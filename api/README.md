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

## Smart Transaction Detection

The backend uses `TransactionContext` (ThreadLocal) to intelligently detect when transactions are created, updated, or deleted through AI tool calls.

### How It Works

1. **TransactionContext.java**: ThreadLocal storage for tracking transaction operations
   ```java
   public static void markTransactionOccurred()
   public static boolean hasTransactionOccurred()
   public static void clear()
   ```

2. **TransactionTools.java**: Marks context after successful operations
   - `createTransaction()` - Marks after creation
   - `updateTransaction()` - Marks after update
   - `deleteTransaction()` - Marks after deletion

3. **ChatService.java**: Checks context and sets response flag
   ```java
   try {
       // AI processes message
       response = assistant.chat(userId, message);
   } finally {
       // Check if transaction occurred
       if (TransactionContext.hasTransactionOccurred()) {
           response.setTransaction(true);
       }
       TransactionContext.clear();
   }
   ```

4. **Frontend**: Receives `transaction: true` flag and triggers balance refresh

### Benefits
- ✅ Only refreshes when actual transactions occur
- ✅ Works for both direct REST calls and AI chat
- ✅ Thread-safe using ThreadLocal
- ✅ Automatic cleanup in finally block
- ✅ No false positives from queries

## Intelligent Date Handling

Transactions default to the current date when not specified:

1. **TransactionTools**: Uses `LocalDateTime.now()` for null/empty dates
2. **FinanceAssistant SystemMessage**: Instructs AI about current date (2026-03-11)
3. **AI Context**: "NEVER assume old dates like 2023, always use recent dates"

This prevents the AI from inferring dates in the past when users don't specify one.

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
├── ai/                           # AI tools for LangChain4j
│   ├── FinanceAssistant.java    # AI service interface with SystemMessage
│   ├── TransactionTools.java    # Transaction CRUD tools (marks context)
│   ├── TransactionContext.java  # ThreadLocal for tracking operations
│   ├── BalanceTools.java        # Balance query tools
│   ├── CategoryTools.java       # Category management tools
│   └── BankAccountTools.java    # Bank account management tools
├── config/                       # Spring configuration
│   ├── ChatMemoryConfig.java    # pgvector chat memory
│   ├── PgVectorConfig.java      # Vector store configuration
│   └── VectorStoreConfig.java   # Embedding configuration
├── controller/                   # REST controllers
│   ├── AssistantController.java # Chat endpoints
│   ├── TransactionController.java
│   ├── CategoryController.java
│   ├── BankAccountController.java
│   ├── BalanceController.java
│   └── dto/                     # Request/Response DTOs
│       ├── ChatResponseDTO.java # Includes transaction flag
│       ├── CategoryBalanceDTO.java
│       └── BankAccountBalanceDTO.java
├── model/                       # JPA entities
│   ├── Transaction.java         # Financial transactions
│   ├── TransactionType.java     # CREDIT/DEBIT enum
│   ├── Category.java
│   ├── BankAccount.java
│   ├── User.java
│   └── Message.java             # Chat history with embeddings
├── repository/                  # Spring Data repositories
│   ├── TransactionRepository.java
│   ├── CategoryRepository.java
│   ├── BankAccountRepository.java
│   ├── MessageRepository.java
│   └── UserRepository.java
└── service/                     # Business logic
    ├── ChatService.java         # AI interaction + context checking
    ├── TransactionService.java
    ├── CategoryService.java
    ├── BankAccountService.java
    └── BalanceService.java
```

## Transaction Types

The system uses CREDIT/DEBIT instead of INCOME/EXPENSE:
- **CREDIT**: Money coming in (income, deposits)
- **DEBIT**: Money going out (expenses, withdrawals)

Defined in `TransactionType.java` enum.

## API Response Format

### Chat Response (ChatResponseDTO)
```json
{
  "message": "Transaction created successfully",
  "status": "success",
  "transaction": true,
  "showRepeat": false
}
```

### Balance Response
```json
{
  "totalBalance": 1500.00,
  "totalCredit": 3000.00,
  "totalDebit": 1500.00
}
```

## Error Handling

- AI tool failures return error status
- Date parsing gracefully defaults to now()
- Database constraints prevent invalid data
- Thread cleanup in finally blocks

## Performance Considerations

- ThreadLocal cleanup prevents memory leaks
- Indexed database queries
- Efficient balance calculations with SQL aggregates
- pgvector for fast semantic search
- Connection pooling with HikariCP
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
