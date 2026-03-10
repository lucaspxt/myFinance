# MyFinance

Personal finance management system with AI-powered assistant using LangChain4j.

## Project Structure

This is a monorepo containing both backend and frontend applications:

```
myFinance/
├── api/                    # Java Spring Boot backend
│   ├── src/               # Java source code
│   ├── pom.xml           # Maven configuration
│   └── TOOL_CALLING_GUIDE.md  # AI tools documentation
├── web/                    # Angular frontend (to be created)
│   └── README.md          # Frontend setup instructions
├── docker-compose.yml     # PostgreSQL + pgvector database
└── README.md             # This file
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose
- Node.js 18+ (for frontend)
- Angular CLI (for frontend)

### 1. Start Database
```bash
docker-compose up -d
```

This will start PostgreSQL with pgvector extension on port 5433.

### 2. Run Backend API
```bash
cd api
mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### 3. Setup Frontend
```bash
cd web
# Follow instructions in web/README.md to create Angular app
ng new myfinance-web --routing --style=scss
ng serve
```

The web application will be available at `http://localhost:4200`

## Features

### Backend (Java Spring Boot)
- RESTful API for financial transactions, categories, and bank accounts
- AI-powered chat assistant using LangChain4j and OpenAI
- 11 AI tools for natural language interaction:
  - Transaction management (create, update, delete, list)
  - Category management (list, create)
  - Bank account management (list, create)
  - Balance queries (total, by account, by category)
- PostgreSQL with pgvector for chat memory and embeddings
- Flyway database migrations

### Frontend (Angular)
- To be implemented
- Will provide UI for all backend features
- AI chat interface

## Environment Variables

Create `application-local.yml` in `api/src/main/resources/` with:

```yaml
spring:
  datasource:
    password: your_postgres_password

langchain4j:
  open-ai:
    chat-model:
      api-key: your_openai_api_key
    embedding-model:
      api-key: your_openai_api_key
```

## API Documentation

See `api/TOOL_CALLING_GUIDE.md` for detailed information about AI tools and capabilities.

## Development

### Backend Development
```bash
cd api
mvn spring-boot:run
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
