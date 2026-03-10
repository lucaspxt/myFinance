# MyFinance - Web Application (Angular)

This directory will contain the Angular frontend application for MyFinance.

## Getting Started

### Prerequisites
- Node.js (v18 or higher)
- npm or yarn
- Angular CLI

### Install Angular CLI
```bash
npm install -g @angular/cli
```

### Create Angular Application
To initialize the Angular application in this directory, run:

```bash
# From the web directory
ng new myfinance-web --directory . --routing --style=scss

# Or if the above doesn't work, run from parent directory:
cd web
ng new myfinance-web --routing --style=scss
# Then move contents from myfinance-web to current directory
```

### Development Server
Once the Angular application is created, run:

```bash
npm install
ng serve
```

Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Project Structure
```
web/
  ├── src/
  │   ├── app/              # Application components
  │   ├── assets/           # Static assets
  │   ├── environments/     # Environment configurations
  │   └── index.html        # Main HTML file
  ├── angular.json          # Angular CLI configuration
  ├── package.json          # npm dependencies
  └── tsconfig.json         # TypeScript configuration
```

## API Integration
The Angular application will connect to the Java backend API running on:
- **API Base URL**: `http://localhost:8080`

## Available Scripts
- `ng serve` - Start development server
- `ng build` - Build the project
- `ng test` - Run unit tests
- `ng e2e` - Run end-to-end tests

## Next Steps
1. Create Angular application using the commands above
2. Set up HTTP client to connect to the backend API
3. Create authentication service
4. Build UI components for transactions, categories, and accounts
5. Implement chat interface for the AI assistant
