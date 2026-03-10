# LangChain4j Tool Calling - Financial Assistant

## Implementation

Implemented LangChain4j tool calling for the financial management system. Now the AI assistant can automatically manage transactions, categories, and bank accounts through natural language!

## Created/Modified Files

### 1. **TransactionTools.java** (new)
Class that exposes 8 tools to the AI assistant:

#### Transaction Management
- **`createTransaction()`**: Creates a new financial transaction
  - Accepts names (not IDs!) for category and bank account
  - Automatically validates if the category and account exist
  - Uses the default account if none is specified
  - Optional `description` parameter to document what the transaction represents (e.g., "Lunch at restaurant", "Monthly rent payment")
  - Optional `transactionDate` parameter in format yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss (default: today)
  - Returns user-friendly success or error messages

- **`updateTransaction()`**: Updates an existing transaction
  - Parameters: transactionId, type, categoryName, bankAccountName, value, description (optional), transactionDate (optional)
  - Validates user ownership before updating
  - Returns confirmation with updated details

- **`deleteTransaction()`**: Deletes a transaction by ID
  - Validates user ownership before deletion
  - Returns deletion confirmation

- **`getTransactionHistory()`**: Lists all user transactions
  - Returns formatted list with ID, Date, Type, Category, Account, Amount, and Description (if present)
  - Automatically filtered by current user

#### Category Management
- **`listCategories()`**: Lists all available user categories

- **`createCategory()`**: Creates a new category
  - Returns category ID and name upon creation

#### Bank Account Management
- **`listBankAccounts()`**: Lists all user bank accounts

- **`createBankAccount()`**: Creates a new bank account
  - Parameters: accountName, setAsDefault
  - Automatically clears previous default if setAsDefault=true
  - Returns account details with [DEFAULT] tag if applicable

### 2. **FinanceAssistant.java** (modified)
Updated `@SystemMessage` to the `chat()` method listing all available tools and instructing the assistant to:
- Use appropriate tools based on user intent
- Automatically execute financial operations
- Confirm actions to the user

### 3. **TransactionService.java** (modified)
Fixed `getAll()` method to filter transactions by current user ID for security.

### 4. **Transaction.java** (modified)
Added `description` field to the Transaction entity to store contextual information about each transaction:
- New field: `private String description;`
- Updated constructors to support optional description parameter
- Description appears in transaction history when present

Added `createdAt` field to the Transaction entity to store the transaction date:
- New field: `private LocalDateTime createdAt;`
- Updated constructors to support optional createdAt parameter (defaults to LocalDateTime.now())
- Transaction date appears in transaction history and confirmation messages

### 5. **V5__add_description_to_transactions.sql** (new)
Flyway database migration to add the description column:
```sql
ALTER TABLE transactions ADD COLUMN description VARCHAR(500);
```

### 6. **V6__add_created_at_to_transactions.sql** (new)
Flyway database migration to add the created_at column:
```sql
ALTER TABLE transactions ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
```

### 7. **BalanceService.java** (new)
Service layer for balance calculations:
- `getTotalBalance()` - calculates net balance (income - expenses)
- `getBalanceByBankAccount()` - balance grouped by account
- `getBalanceByCategory()` - balance grouped by category

### 8. **BalanceTools.java** (new)
Class that exposes 3 balance query tools to the AI assistant:
- `getTotalBalance()` - returns formatted total balance with emoji indicator
- `getBalanceByBankAccount()` - lists balance per account
- `getBalanceByCategory()` - lists balance per category with CREDIT/DEBIT indicators

## How It Works

1. User sends a natural language message like: *"I spent $50 at the grocery store on food"*

2. The AI assistant (FinanceAssistant):
   - Analyzes the message using the LLM
   - Identifies the intent (create transaction, list data, etc.)
   - **Automatically selects and calls the appropriate tool**
   - Passes extracted parameters to the tool method

3. The tool (TransactionTools):
   - Validates parameters (types, names, permissions)
   - Searches for entities in the database (categories, accounts)
   - Calls the appropriate service method
   - Returns a user-friendly success or error message

4. The AI assistant:
   - Receives the tool execution result
   - Incorporates it into a natural language response
   - Returns the final message to the user

### Key Features

- **Zero Configuration**: Tools are automatically discovered via `@Tool` annotation
- **Type Safety**: Parameters are validated before execution
- **Security**: User ownership is verified for all operations
- **Natural Language**: Users don't need to know tool names or exact syntax
- **Context Aware**: AI maintains conversation context and can chain multiple tools

## Usage Examples

### Transaction Management:

**User:** "I received my salary of $5000"
**Assistant:** *[calls createTransaction automatically]* "✅ Transaction created! Your salary of $5000 has been registered as income."

**User:** "I paid $150 for the electricity bill on March 5th"
**Assistant:** *[calls createTransaction with description and date]* "✅ Transaction created successfully!\nType: Expense\nCategory: Bills\nAccount: Main Account\nAmount: $150.00\nDescription: Electricity bill\nDate: 2026-03-05 00:00"

**User:** "I spent $45.50 at Starbucks for coffee and snacks yesterday"
**Assistant:** *[calls createTransaction with date]* "✅ Transaction created successfully!\nType: Expense\nCategory: Food\nAccount: Wallet\nAmount: $45.50\nDescription: Coffee and snacks at Starbucks\nDate: 2026-03-09 00:00"

**User:** "Update transaction 42 to $175 in the Utilities category with description Monthly internet service"
**Assistant:** *[calls updateTransaction]* "✅ Transaction updated successfully! ID: 42, Amount: $175, Category: Utilities, Description: Monthly internet service"

**User:** "Delete transaction 15"
**Assistant:** *[calls deleteTransaction]* "✅ Transaction #15 deleted successfully!"

**User:** "Show my transaction history"
**Assistant:** *[calls getTransactionHistory]* "Transaction History:\n\nID: 1 | 2026-03-10 14:30 | Income | Salary | Main Account | $5000.00\nID: 2 | 2026-03-09 00:00 | Expense | Food | Wallet | $45.50 | Coffee and snacks at Starbucks..."

### Category Management:

**User:** "What categories do I have?"
**Assistant:** *[calls listCategories]* "Available categories:\n- Food\n- Transportation\n- Salary\n- Bills..."

**User:** "Create a new category called Investments"
**Assistant:** *[calls createCategory]* "✅ Category created successfully!\nID: 8\nName: Investments"

### Bank Account Management:

**User:** "List my bank accounts"
**Assistant:** *[calls listBankAccounts]* "Available bank accounts:\n- Main Account [DEFAULT]\n- Savings\n- Wallet"

**User:** "Create a new account called Credit Card as default"
**Assistant:** *[calls createBankAccount]* "✅ Bank account created successfully!\nID: 5\nName: Credit Card [DEFAULT]"

## Implementation Advantages

1. **Intelligent**: AI decides when to call tools based on context
2. **User-friendly**: Uses names instead of IDs (more natural for users)
3. **Robust**: Validates inputs and provides clear error messages
4. **Secure**: Validates user ownership before modifications
5. **Extensible**: Easy to add new tools for other operations
6. **Complete**: Full CRUD operations for transactions, categories, and accounts

## Available Tools Summary

| Tool | Purpose | Parameters |
|------|---------|------------|
| `createTransaction` | Create new transaction | type, categoryName, bankAccountName, value, description (optional), transactionDate (optional) |
| `updateTransaction` | Update existing transaction | transactionId, type, categoryName, bankAccountName, value, description (optional), transactionDate (optional) |
| `deleteTransaction` | Delete transaction | transactionId |
| `getTransactionHistory` | List all transactions | none |
| `listCategories` | List all categories | none |
| `createCategory` | Create new category | categoryName |
| `listBankAccounts` | List all bank accounts | none |
| `createBankAccount` | Create new account | accountName, setAsDefault |
| `getTotalBalance` | Get total balance | none |
| `getBalanceByBankAccount` | Get balance per account | none |
| `getBalanceByCategory` | Get balance per category | none |

## Next Steps (Optional)

You can extend the system by adding more tools:

- `updateCategory()` - modify category names
- `deleteCategory()` - remove unused categories
- `updateBankAccount()` - modify account details
- `deleteBankAccount()` - remove accounts
- `getBalanceSummary()` - show balance by category/account
- `getTransactionsByCategory()` - filter transactions by category
- `getTransactionsByDateRange()` - filter by date period

Just add methods annotated with `@Tool` in the `TransactionTools` class or create new tool classes!

## Testing

To test, start the application and send messages through the chat endpoint:

### Create Transaction
```bash
POST http://localhost:8080/api/assistant/chat
Content-Type: application/json

{
  "message": "I spent $100 at the supermarket on food"
}
```

### Update Transaction
```bash
POST http://localhost:8080/api/assistant/chat
Content-Type: application/json

{
  "message": "Update transaction 5 to $125"
}
```

### Delete Transaction
```bash
POST http://localhost:8080/api/assistant/chat
Content-Type: application/json

{
  "message": "Delete transaction 10"
}
```

### View History
```bash
POST http://localhost:8080/api/assistant/chat
Content-Type: application/json

{
  "message": "Show me my transaction history"
}
```

### Create Category
```bash
POST http://localhost:8080/api/assistant/chat
Content-Type: application/json

{
  "message": "Create a category called Entertainment"
}
```

### Create Bank Account
```bash
POST http://localhost:8080/api/assistant/chat
Content-Type: application/json

{
  "message": "Create a new bank account called Savings"
}
```

The AI will automatically:
- Identify the user's intent
- Call the appropriate tool(s)
- Validate the data
- Execute the operation
- Return a confirmation message
