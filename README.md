# MyFinance

Personal finance management system with AI-powered chat assistant using LangChain4j and Angular.

## Project Structure

This is a monorepo containing both backend and frontend applications:

```
myFinance/
├── api/                    # Java Spring Boot backend
│   ├── src/               # Java source code
│   ├── pom.xml           # Maven configuration
│   ├── README.md         # Backend documentation
│   └── TOOL_CALLING_GUIDE.md  # AI tools documentation
├── web/                    # Angular 19 frontend
│   ├── src/               # Angular source code
│   ├── package.json      # npm dependencies
│   └── README.md         # Frontend documentation
├── docker-compose.yml     # PostgreSQL + pgvector database
├── start-dev.ps1         # PowerShell script to start all services
└── README.md             # This file
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose
- Node.js 18+
- Angular CLI 19+

### Option 1: Start All Services (Windows)
```powershell
.\start-dev.ps1
```

This script will:
1. Start PostgreSQL database
2. Start backend API on port 8080
3. Start Angular dev server on port 4200

### Option 2: Manual Start

#### 1. Start Database
```bash
docker-compose up -d
```

#### 2. Configure Backend
Create `api/src/main/resources/application-local.yml`:
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

#### 3. Run Backend
```bash
cd api
mvn spring-boot:run
```

#### 4. Run Frontend
```bash
cd web
npm install
npm start
```

Access the application at `http://localhost:4200`

## Features

### Frontend (Angular 19)
- 🤖 **AI Chat Interface** - Natural language interaction with financial data
- 💬 **Real-time Chat** - Markdown rendering with code highlighting
- 📊 **Smart Sidebar** - Categories and bank accounts with live balances
- 🔄 **Auto-refresh** - Balance updates after transactions
- 📱 **Responsive Design** - Mobile-first layout (hides sidebar on <992px)
- 🌍 **Internationalization** - 6 languages (EN, PT, ES, FR, DE, IT)
- ✨ **Skeleton Loading** - Smooth loading states for sidebar and modals
- 📋 **Transaction Modal** - View filtered transactions by category/account
- 🎨 **Modern UI** - Toggle-style tabs, color-coded balances
- 🍪 **Cookie Consent** - GDPR-compliant cookie banner
- 🔒 **Privacy & Terms** - Static pages for legal content

### Backend (Spring Boot 3.5.3)
- 🤖 **AI Assistant** - OpenAI GPT-4 with LangChain4j integration
- 🧠 **Smart Context** - ThreadLocal-based transaction detection
- 📅 **Intelligent Dates** - Auto-defaults to current date (2026-03-11)
- 🛠️ **11 AI Tools** - Natural language CRUD operations
- 💾 **PostgreSQL + pgvector** - Vector embeddings for chat memory
- 🔄 **Flyway Migrations** - Version-controlled database schema
- 📝 **RESTful API** - Complete CRUD for transactions, categories, accounts
- 💰 **Balance Calculations** - Total, by account, by category

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 17
- **AI**: LangChain4j 1.0.0-beta5 + OpenAI GPT-4
- **Database**: PostgreSQL + pgvector extension
- **Migrations**: Flyway
- **Build**: Maven

### Frontend
- **Framework**: Angular 19 (standalone components)
- **Language**: TypeScript
- **State**: RxJS observables
- **i18n**: ngx-translate
- **Styling**: SCSS with CSS custom properties
- **Icons**: Font Awesome
- **Markdown**: ngx-markdown with Prism.js
- **Build**: Angular CLI + esbuild

## Application Architecture

### Frontend Structure
```
web/src/app/
├── components/          # Reusable components
│   ├── layout/         # Header, footer
│   └── cookie-consent/ # GDPR banner
├── pages/              # Route components
│   ├── chat/          # Main chat interface with sidebar
│   ├── privacy/       # Privacy policy
│   └── terms/         # Terms of service
├── services/          # API services and state management
│   ├── chat.service.ts
│   ├── balance-refresh.service.ts
│   └── transaction.service.ts
├── models/            # TypeScript interfaces
└── shared/            # Pipes, utilities
    ├── pipes/         # Currency formatting
    └── message/       # Chat message component
```

### Backend Structure
```
api/src/main/java/com/myfinance/
├── ai/                    # LangChain4j AI tools
│   ├── FinanceAssistant.java    # AI service interface
│   ├── TransactionTools.java    # Transaction CRUD tools
│   ├── TransactionContext.java  # ThreadLocal context tracking
│   └── BalanceTools.java        # Balance query tools
├── controller/               # REST endpoints
│   ├── AssistantController.java
│   ├── TransactionController.java
│   └── dto/                 # Request/Response DTOs
├── model/                   # JPA entities
│   ├── Transaction.java
│   ├── TransactionType.java (CREDIT/DEBIT)
│   └── Category.java
├── repository/             # Spring Data JPA
└── service/               # Business logic
    └── ChatService.java   # AI interaction + context management
```

## Key Features Explained

### Smart Transaction Detection
The backend uses `TransactionContext` (ThreadLocal) to track when AI tools create/modify transactions, ensuring balance refresh only happens when needed.

### Multi-language Support
Supported languages with route-based switching:
- English (EN)
- Portuguese (PT)
- Spanish (ES)
- French (FR)
- German (DE)
- Italian (IT)

### Responsive Sidebar
- **Desktop (≥992px)**: Sidebar (25%) + Chat (75%)
- **Mobile (<992px)**: Chat only (100%), sidebar hidden

### Transaction Modal
Click any category or bank account in the sidebar to view filtered transactions with:
- Description/Category name
- Transaction date
- Value with color coding (green=CREDIT, red=DEBIT)

## Development

### Run Tests
```bash
# Backend
cd api
mvn test

# Frontend
cd web
npm test
```

### Build for Production
```bash
# Backend
cd api
mvn clean package

# Frontend
cd web
npm run build
```

### Code Quality
```bash
# Format backend code
cd api
mvn spotless:apply

# Lint frontend
cd web
npm run lint
```

## API Endpoints

### Transactions
- `GET /api/transactions` - List all
- `POST /api/transactions` - Create
- `PUT /api/transactions/{id}` - Update
- `DELETE /api/transactions/{id}` - Delete

### Categories
- `GET /api/categories` - List all
- `POST /api/categories` - Create

### Bank Accounts
- `GET /api/bank-accounts` - List all
- `POST /api/bank-accounts` - Create

### Balance
- `GET /api/balance/total` - Total balance
- `GET /api/balance/by-bank-account` - By account
- `GET /api/balance/by-category` - By category

### AI Assistant
- `POST /api/assistant/chat` - Send message
- `GET /api/assistant/history/{userId}` - Chat history

## Environment Configuration

### Backend (`api/src/main/resources/application-local.yml`)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/myfinance
    username: postgres
    password: postgres
  
langchain4j:
  open-ai:
    chat-model:
      api-key: sk-...
      model-name: gpt-4
      temperature: 0.7
    embedding-model:
      api-key: sk-...
```

### Frontend (uses proxy for API calls)
Proxy configuration in `web/proxy.conf.json` redirects `/api/*` to `http://localhost:8080`

## Database Schema

Tables:
- `users` - User accounts
- `categories` - Expense/Income categories (user-scoped)
- `bank_accounts` - Bank accounts (user-scoped)
- `transactions` - Financial transactions (CREDIT/DEBIT)
- `messages` - Chat message history with embeddings

## Contributing

1. Create a feature branch
2. Make changes
3. Run tests
4. Submit pull request

## Troubleshooting

### Database connection failed
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Restart database
docker-compose restart
```

### Backend won't start
```bash
# Check Java version
java -version  # Should be 17+

# Clean build
cd api
mvn clean install
```

### Frontend errors
```bash
# Clear node_modules and reinstall
cd web
rm -rf node_modules package-lock.json
npm install
```

### OpenAI API errors
- Verify API key is valid
- Check rate limits
- Ensure sufficient credits

## License

Private project

## Documentation

- [`api/README.md`](api/README.md) - Backend details
- [`api/TOOL_CALLING_GUIDE.md`](api/TOOL_CALLING_GUIDE.md) - AI tools documentation
- [`web/README.md`](web/README.md) - Frontend setup
- [`web/INTEGRATION_GUIDE.md`](web/INTEGRATION_GUIDE.md) - Frontend integration guide
```

### Frontend Development
```bash
cd web
ng serve
```

### Run Tests
```bash
# Backend tests
cd api
mvn test

# Frontend tests (once created)
cd web
ng test
```

## Technology Stack

### Backend
- Spring Boot 3.5.3
- Java 17
- LangChain4j 1.0.0-beta5
- PostgreSQL + pgvector
- Flyway
- Lombok

### Frontend
- Angular (to be installed)
- TypeScript
- RxJS
- Angular Material (optional)

## License

Private project
