# MyFinance Web - Angular Frontend

Angular 19 standalone application providing a modern UI for the MyFinance personal finance management system with AI-powered chat assistant.

## Features

### 🤖 AI Chat Interface
- Natural language interaction with financial data
- Real-time markdown rendering with code highlighting (Prism.js)
- Suggested questions for quick interactions
- Message history with user/assistant differentiation
- Loading states with spinner overlay
- Error handling with retry button

### 📊 Smart Sidebar
- **Categories View**: List all expense categories with current balances
- **Accounts View**: List all bank accounts with current balances
- **Toggle Tabs**: Switch between categories and accounts view
- **Color Coding**: 
  - Green (positive balance)
  - Red (negative balance)
  - Primary color (zero balance)
- **Default Indicator**: Star icon for default bank account
- **Skeleton Loading**: Shimmer effect during data loading
- **Click to Details**: Opens modal with filtered transactions

### 📋 Transaction Modal
- View transactions filtered by category or bank account
- Display transaction details:
  - Description or category name
  - Transaction date (formatted)
  - Value with +/- prefix and color coding
- Smooth animations (fade-in overlay, slide-up content)
- Skeleton loading with shimmer effect
- Empty state message
- Click outside or X button to close

### 🔄 Auto-refresh System
- Balance refresh service using RxJS observables
- Automatically updates sidebar after transactions
- Header balance updates in sync
- Parallel data loading with `forkJoin`

### 📱 Responsive Design
- Mobile-first approach
- **Desktop (≥992px)**: Sidebar (3 cols) + Chat (9 cols)
- **Mobile (<992px)**: Chat only (12 cols), sidebar hidden
- Adaptive layouts for different screen sizes

### 🌍 Internationalization (i18n)
Supported languages with route-based switching:
- 🇺🇸 English (EN)
- 🇧🇷 Português (PT)
- 🇪🇸 Español (ES)
- 🇫🇷 Français (FR)
- 🇩🇪 Deutsch (DE)
- 🇮🇹 Italiano (IT)

Translation files: `src/assets/i18n/{lang}.json`

### 🍪 Cookie Consent
- GDPR-compliant cookie banner
- Accept/Decline options
- Persistent storage of user choice
- Customizable appearance

### 🎨 Modern UI/UX
- CSS custom properties for theming
- Toggle-style tabs with smooth transitions
- Hover effects and micro-interactions
- Card-based layouts
- Font Awesome icons
- Brazilian currency formatting (1.234,56)

## Project Structure

```
web/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── cookie-consent/      # GDPR banner
│   │   │   ├── lang-redirect/       # Language routing
│   │   │   └── layout/
│   │   │       └── header/          # App header with balance
│   │   ├── pages/
│   │   │   ├── chat/               # Main chat interface
│   │   │   │   ├── chat.component.ts
│   │   │   │   ├── chat.component.html
│   │   │   │   └── chat.component.scss
│   │   │   ├── privacy/            # Privacy policy
│   │   │   └── terms/              # Terms of service
│   │   ├── services/
│   │   │   ├── app.service.ts             # App state
│   │   │   ├── balance.service.ts         # Balance API
│   │   │   ├── balance-refresh.service.ts # Refresh observable
│   │   │   ├── bank-account.service.ts    # Bank accounts API
│   │   │   ├── category.service.ts        # Categories API
│   │   │   ├── chat.service.ts            # Chat API
│   │   │   ├── transaction.service.ts     # Transactions API
│   │   │   ├── user.service.ts            # User API
│   │   │   └── i18n/                      # Translation loader
│   │   ├── shared/
│   │   │   ├── message/            # Chat message component
│   │   │   ├── pipes/              # Currency formatting
│   │   │   ├── repeat-button/      # Retry button
│   │   │   └── social-links/       # Footer links
│   │   ├── models/                 # TypeScript interfaces
│   │   ├── app.config.ts           # App configuration
│   │   └── app.routes.ts           # Route definitions
│   ├── assets/
│   │   └── i18n/                   # Translation JSON files
│   ├── styles.scss                 # Global styles
│   └── index.html                  # Entry point
├── proxy.conf.json                 # API proxy configuration
├── angular.json                    # Angular CLI config
├── package.json                    # npm dependencies
└── tsconfig.json                   # TypeScript config
```

## Getting Started

### Prerequisites
- Node.js 18+
- npm 9+
- Angular CLI 19+

### Installation
```bash
npm install
```

### Development Server
```bash
npm start
# or
ng serve
```

Navigate to `http://localhost:4200/`

The app will automatically reload if you change any source files.

### API Proxy
The development server proxies API calls from `/api/*` to `http://localhost:8080` (Spring Boot backend).

Configuration in `proxy.conf.json`.

## Available Scripts

### Development
```bash
npm start              # Start dev server
npm run build          # Build for production
npm run watch          # Build in watch mode
npm test               # Run unit tests
npm run lint           # Lint TypeScript
```

### Production Build
```bash
npm run build
```

Output in `dist/` directory, optimized for production.

## Technology Stack

- **Framework**: Angular 19 (standalone components)
- **Language**: TypeScript 5.7
- **State Management**: RxJS 7.8
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router
- **i18n**: @ngx-translate/core 17.0
- **Markdown**: ngx-markdown 19.0
- **Syntax Highlighting**: Prism.js
- **Icons**: Font Awesome 6.x
- **Styling**: SCSS + CSS Custom Properties
- **Build**: Angular CLI + esbuild

## Key Components

### ChatComponent
Main interface with:
- Chat messages area with auto-scroll
- Input field for user messages
- Sidebar with categories/accounts
- Transaction modal
- Loading states
- Suggested questions

### HeaderComponent
- Application title
- Total balance display
- Language selector
- Responsive design

### MessageComponent
- User/Assistant message differentiation
- Markdown rendering
- Code syntax highlighting
- Timestamp display

## Services

### ChatService
- Send messages to AI assistant
- Retrieve chat history
- Error handling

### BalanceRefreshService
- Observable-based refresh trigger
- Used by Header and Sidebar
- Triggers after transaction operations

### TransactionService
- CRUD operations for transactions
- Types: CREDIT (income) / DEBIT (expense)
- Date formatting

### CategoryService / BankAccountService
- List all categories/accounts
- Filter archived items
- Create new entries

### BalanceService
- Get total balance
- Get balance by category
- Get balance by bank account

## Styling

### CSS Custom Properties
```scss
--primary-color: #007bff
--green: #00bb00
--red: #ff4444
--white: #ffffff
--black: #333333
--background-gray: #f5f5f5
--border-color: #e0e0e0
```

### Responsive Breakpoints
- Mobile: < 767px
- Tablet: 768px - 991px
- Desktop: ≥ 992px

## Internationalization

### Adding a New Language

1. Create translation file: `src/assets/i18n/xx.json`
2. Add language to `app.config.ts`:
```typescript
availableLanguages: ['en', 'pt', 'es', 'fr', 'de', 'it', 'xx']
```

3. Update header language selector

### Translation Keys Structure
```json
{
  "CHAT": {
    "HEADER": "...",
    "WELCOME_MESSAGE": "...",
    "SIDEBAR": {
      "CATEGORIES": "...",
      "ACCOUNTS": "..."
    },
    "MODAL": {
      "NO_TRANSACTIONS": "..."
    }
  },
  "LANGUAGE": {...},
  "COOKIES": {...}
}
```

## Testing

### Unit Tests
```bash
npm test
```

Tests use Karma + Jasmine.

### E2E Tests
```bash
npm run e2e
```

## Deployment

### Build for Production
```bash
npm run build
```

### Server-Side Rendering (SSR)
The app is configured for SSR with `@angular/ssr`.

Build and serve:
```bash
npm run build:ssr
npm run serve:ssr
```

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Performance Optimizations

- Standalone components (no NgModules)
- OnPush change detection (where applicable)
- Lazy loading for routes
- RxJS `takeUntil` for subscription cleanup
- `forkJoin` for parallel API calls
- CSS animations with GPU acceleration
- esbuild for fast compilation

## Accessibility

- Semantic HTML
- ARIA labels where needed
- Keyboard navigation support
- Focus management in modals
- Color contrast ratios (WCAG AA)

## Environment Variables

No environment variables needed for development. API URL is proxied.

For production, configure API base URL in `app.config.ts`.

## Troubleshooting

### Port already in use
```bash
# Kill process on port 4200
npx kill-port 4200
```

### Module not found
```bash
rm -rf node_modules package-lock.json
npm install
```

### Translation not loading
- Check `src/assets/i18n/{lang}.json` exists
- Verify language code in URL matches filename
- Check browser console for HTTP errors

## Contributing

1. Follow Angular style guide
2. Use standalone components
3. Add translations for all new text
4. Write unit tests for services
5. Keep components focused and small

## Documentation

- [Angular Documentation](https://angular.dev)
- [RxJS Documentation](https://rxjs.dev)
- [ngx-translate](https://github.com/ngx-translate/core)
- [ngx-markdown](https://github.com/jfcere/ngx-markdown)

## License

Private project
