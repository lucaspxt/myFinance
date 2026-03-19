# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Environment variable configuration support for all sensitive data
- `.env.example` template file with all required variables
- Comprehensive documentation following open source best practices
- `CONTRIBUTING.md` with detailed contribution guidelines
- `SECURITY.md` with security policies and best practices
- `CODE_OF_CONDUCT.md` based on Contributor Covenant
- GitHub issue templates for bugs and feature requests
- Pull request template for standardized contributions
- MIT License file
- Security best practices section in README
- Environment variables reference table in README

### Changed
- Docker Compose configuration now uses environment variables
- `application.yml` now uses consistent environment variable names
- `start-dev.ps1` script now loads and validates environment variables
- README reorganized with better structure and navigation
- All database credentials are now parameterized
- OpenAI API key and model configurations are externalized

### Security
- Database credentials no longer hardcoded
- API keys moved to environment variables
- Added security checklist for production deployments
- Improved documentation on securing sensitive information

### Documentation
- Added badges and quick navigation links to README
- Expanded Quick Start guide with detailed configuration steps
- Added troubleshooting section
- Documented all environment variables with descriptions
- Added links to all documentation files
- Improved project structure documentation

## [1.0.0] - 2026-03-19

### Added
- Initial release of MyFinance
- AI-powered chat interface using LangChain4j and OpenAI
- Natural language transaction management
- Real-time balance calculations
- Multi-language support (EN, PT, ES, FR, DE, IT)
- PostgreSQL database with pgvector extension
- Spring Boot 3.5.3 backend with Java 21
- Angular 19 frontend with standalone components
- RESTful API for transactions, categories, and bank accounts
- Flyway database migrations
- Docker Compose setup for PostgreSQL
- Responsive design with mobile support
- Transaction filtering and modal views
- GDPR-compliant cookie consent
- Privacy and Terms pages

### Backend Features
- LangChain4j AI assistant integration
- 11 AI tools for natural language operations
- Thread-local context tracking
- Smart transaction detection
- Vector embeddings for chat memory
- Complete CRUD operations
- Balance calculations (total, by account, by category)

### Frontend Features
- Intelligent chat interface
- Smart sidebar with live balances
- Transaction management with inline editing
- Category and bank account filtering
- Skeleton loading states
- Markdown rendering with code highlighting
- Color-coded balance indicators
- Internationalization support

---

## How to Read This Changelog

- **Added** for new features
- **Changed** for changes in existing functionality
- **Deprecated** for soon-to-be removed features
- **Removed** for now removed features
- **Fixed** for any bug fixes
- **Security** for vulnerability fixes and security improvements

## Version Format

This project uses [Semantic Versioning](https://semver.org/):
- **MAJOR** version for incompatible API changes
- **MINOR** version for backwards-compatible functionality additions
- **PATCH** version for backwards-compatible bug fixes

## Links

- [Unreleased changes](https://github.com/your-username/myFinance/compare/v1.0.0...HEAD)
- [v1.0.0](https://github.com/your-username/myFinance/releases/tag/v1.0.0)
