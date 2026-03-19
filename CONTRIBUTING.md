# Contributing to MyFinance

Thank you for your interest in contributing to MyFinance! We appreciate your help in making this project better.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for everyone. Please:

- Be respectful and inclusive of all perspectives and experiences
- Give and gracefully accept constructive feedback
- Focus on what is best for the community
- Show empathy towards other community members

### Unacceptable Behavior

- Harassment, trolling, or discriminatory comments
- Personal or political attacks
- Publishing others' private information
- Other conduct that could reasonably be considered inappropriate

## Getting Started

1. **Fork the repository** to your GitHub account
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/your-username/myFinance.git
   cd myFinance
   ```
3. **Add the upstream remote**:
   ```bash
   git remote add upstream https://github.com/original-owner/myFinance.git
   ```
4. **Create a `.env` file** from `.env.example`:
   ```bash
   cp .env.example .env
   ```
5. **Configure your environment variables** (see README.md for details)

## Development Setup

### Prerequisites

- Java 21+
- Maven 3.6+
- Docker & Docker Compose
- Node.js 18+
- Angular CLI 19+

### Running Locally

```powershell
# Windows
.\start-dev.ps1

# Or manually
docker-compose up -d
cd api && mvn spring-boot:run
cd web && npm install && npm start
```

Access the application at http://localhost:4200

## How to Contribute

### Types of Contributions We Welcome

- 🐛 **Bug Fixes** - Fix issues and improve stability
- ✨ **New Features** - Add new functionality (discuss in an issue first)
- 📝 **Documentation** - Improve README, guides, or code comments
- 🌍 **Translations** - Add support for new languages
- 🎨 **UI/UX** - Enhance the user interface and experience
- ⚡ **Performance** - Optimize code and queries
- 🧪 **Tests** - Add unit or integration tests
- 🔧 **DevOps** - Improve build, deployment, or CI/CD

### Before You Start

1. **Search existing issues** - Someone might already be working on it
2. **Open an issue** - For major changes, discuss your approach first
3. **Get feedback** - Make sure your idea aligns with project goals

## Pull Request Process

### 1. Create a Branch

Create a feature branch from `main`:

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/bug-description
```

Branch naming conventions:
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test additions
- `chore/` - Maintenance tasks

### 2. Make Your Changes

- Write clean, maintainable code
- Follow existing code style and conventions
- Add comments for complex logic
- Update documentation as needed
- Test your changes thoroughly

### 3. Commit Your Changes

Use conventional commit messages:

```bash
git commit -m "feat: add transaction bulk import feature"
git commit -m "fix: resolve balance calculation rounding error"
git commit -m "docs: update API endpoint documentation"
```

Commit message format:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code style changes (formatting, etc.)
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks
- `perf:` - Performance improvements

### 4. Keep Your Fork Updated

```bash
git fetch upstream
git rebase upstream/main
```

### 5. Push Your Changes

```bash
git push origin feature/your-feature-name
```

### 6. Submit a Pull Request

1. Go to your fork on GitHub
2. Click "New Pull Request"
3. Select your feature branch
4. Fill out the PR template:
   - **Title**: Clear, descriptive title
   - **Description**: What changes you made and why
   - **Related Issues**: Link to related issues
   - **Testing**: How you tested the changes
   - **Screenshots**: If UI changes, include before/after images

### 7. Code Review

- Be patient - reviews take time
- Respond to feedback constructively
- Make requested changes promptly
- Keep discussions focused and respectful

## Coding Standards

### Java (Backend)

- Follow Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and small
- Add JavaDoc for public APIs
- Use Spring Boot best practices
- Handle exceptions appropriately

Example:
```java
/**
 * Creates a new transaction for the specified user.
 * 
 * @param userId the ID of the user
 * @param transaction the transaction details
 * @return the created transaction
 * @throws IllegalArgumentException if transaction is invalid
 */
public Transaction createTransaction(Long userId, Transaction transaction) {
    // Implementation
}
```

### TypeScript (Frontend)

- Use TypeScript strict mode
- Follow Angular style guide
- Use observables for async operations
- Keep components focused
- Use interfaces for data models
- Add JSDoc comments for complex functions

Example:
```typescript
/**
 * Fetches transactions filtered by category.
 * 
 * @param categoryId - The category ID to filter by
 * @returns Observable of transactions
 */
getTransactionsByCategory(categoryId: number): Observable<Transaction[]> {
  return this.http.get<Transaction[]>(`/api/transactions/category/${categoryId}`);
}
```

### General Guidelines

- Write self-documenting code
- Add comments for "why", not "what"
- Keep functions small and focused
- Avoid code duplication (DRY principle)
- Use meaningful names for variables and functions
- Handle errors gracefully
- Write tests for new functionality

## Reporting Bugs

### Before Reporting

1. **Check existing issues** - Has it been reported already?
2. **Try the latest version** - Is the bug still present?
3. **Gather information** - Steps to reproduce, error messages, logs

### Creating a Bug Report

Include:
- **Clear title** - Describe the issue briefly
- **Description** - What happened vs. what you expected
- **Steps to reproduce** - Numbered steps to recreate the bug
- **Environment** - OS, Java version, Node version, browser
- **Screenshots** - If applicable
- **Error messages** - Console logs or stack traces
- **Code samples** - Minimal reproducible example

Example:
```markdown
## Bug Description
Balance calculation shows incorrect total after deleting a transaction.

## Steps to Reproduce
1. Create a transaction with value 100
2. Delete the transaction
3. Check the balance in sidebar

## Expected Behavior
Balance should update to exclude the deleted transaction.

## Actual Behavior
Balance still includes the deleted transaction until page refresh.

## Environment
- OS: Windows 11
- Java: 21.0.1
- Browser: Chrome 120
```

## Suggesting Features

### Before Suggesting

1. **Check existing issues** - Has it been suggested already?
2. **Consider scope** - Does it fit the project goals?
3. **Think about users** - Who benefits and how?

### Creating a Feature Request

Include:
- **Clear title** - Describe the feature briefly
- **Problem statement** - What problem does it solve?
- **Proposed solution** - How should it work?
- **Alternatives** - Other approaches you considered
- **Examples** - Screenshots or mockups if applicable
- **Use cases** - Real-world scenarios

Example:
```markdown
## Feature Request
Add ability to export transactions to CSV.

## Problem
Users want to analyze their data in Excel but can't export it.

## Proposed Solution
Add an "Export" button that downloads transactions as CSV file with columns:
Date, Description, Amount, Type, Category, Bank Account

## Alternatives
- Export as JSON
- Export as PDF
- Integration with Google Sheets

## Use Cases
- Tax preparation
- Financial analysis
- Data backup
```

## Development Resources

- **Backend Documentation**: [api/README.md](api/README.md)
- **Frontend Documentation**: [web/README.md](web/README.md)
- **AI Tools Guide**: [api/TOOL_CALLING_GUIDE.md](api/TOOL_CALLING_GUIDE.md)
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Angular**: https://angular.dev
- **LangChain4j**: https://docs.langchain4j.dev

## Questions?

- Open an issue for bugs or features
- Start a discussion for general questions
- Check the documentation first

---

Thank you for contributing to MyFinance! Your efforts help make this project better for everyone. 🙏
