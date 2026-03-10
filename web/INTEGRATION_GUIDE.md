# Angular + Spring Boot Integration Guide

This guide will help you integrate the Angular frontend with the Spring Boot backend.

## Environment Configuration

### 1. Create Environment Files

Create `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

Create `src/environments/environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  apiUrl: '/api'  // In production, assume API is on same domain
};
```

## HTTP Service Setup

### 2. Create API Service

```bash
ng generate service services/api
```

In `src/app/services/api.service.ts`:
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Transactions
  getTransactions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/transactions`);
  }

  createTransaction(transaction: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/transactions`, transaction);
  }

  updateTransaction(id: number, transaction: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/transactions/${id}`, transaction);
  }

  deleteTransaction(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/transactions/${id}`);
  }

  // Categories
  getCategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/categories`);
  }

  createCategory(category: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/categories`, category);
  }

  // Bank Accounts
  getBankAccounts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/bank-accounts`);
  }

  createBankAccount(account: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/bank-accounts`, account);
  }

  // Balance
  getTotalBalance(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/balance/total`);
  }

  getBalanceByAccount(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/balance/by-bank-account`);
  }

  getBalanceByCategory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/balance/by-category`);
  }

  // AI Assistant
  sendChatMessage(userId: number, message: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/assistant/chat`, {
      userId,
      message
    });
  }

  getChatHistory(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/assistant/history/${userId}`);
  }
}
```

## CORS Configuration (Already done in Backend)

The Spring Boot backend already has CORS configured. No additional setup needed.

## HttpClient Import

In `src/app/app.config.ts` (Angular 17+) or `src/app/app.module.ts`:
```typescript
import { provideHttpClient } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    // ... other providers
  ]
};
```

## Example Component Usage

```typescript
import { Component, OnInit } from '@angular/core';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html'
})
export class TransactionsComponent implements OnInit {
  transactions: any[] = [];
  categories: any[] = [];
  accounts: any[] = [];

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.apiService.getTransactions().subscribe(data => {
      this.transactions = data;
    });

    this.apiService.getCategories().subscribe(data => {
      this.categories = data;
    });

    this.apiService.getBankAccounts().subscribe(data => {
      this.accounts = data;
    });
  }

  createTransaction(transaction: any) {
    this.apiService.createTransaction(transaction).subscribe(() => {
      this.loadData(); // Reload after creation
    });
  }
}
```

## Chat Interface Example

```typescript
import { Component } from '@angular/core';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-chat',
  template: `
    <div class="chat-container">
      <div class="messages">
        <div *ngFor="let msg of messages" [class]="msg.type">
          {{ msg.content }}
        </div>
      </div>
      <div class="input-area">
        <input 
          [(ngModel)]="userMessage" 
          (keyup.enter)="sendMessage()"
          placeholder="Ask me anything about your finances...">
        <button (click)="sendMessage()">Send</button>
      </div>
    </div>
  `
})
export class ChatComponent {
  messages: any[] = [];
  userMessage: string = '';
  userId: number = 1; // Get from auth service

  constructor(private apiService: ApiService) {
    this.loadHistory();
  }

  loadHistory() {
    this.apiService.getChatHistory(this.userId).subscribe(history => {
      this.messages = history.map(h => ({
        type: h.type,
        content: h.content
      }));
    });
  }

  sendMessage() {
    if (!this.userMessage.trim()) return;

    this.messages.push({
      type: 'user',
      content: this.userMessage
    });

    this.apiService.sendChatMessage(this.userId, this.userMessage)
      .subscribe(response => {
        this.messages.push({
          type: 'assistant',
          content: response.response
        });
        this.userMessage = '';
      });
  }
}
```

## TypeScript Interfaces (Optional)

Create `src/app/models/`:

```typescript
// transaction.model.ts
export interface Transaction {
  id?: number;
  type: 'CREDIT' | 'DEBIT';
  category: Category;
  bankAccount: BankAccount;
  value: number;
  description?: string;
  createdAt?: Date;
}

// category.model.ts
export interface Category {
  id?: number;
  name: string;
}

// bank-account.model.ts
export interface BankAccount {
  id?: number;
  name: string;
  defaultAccount: boolean;
}
```

## Next Steps

1. Create Angular components for:
   - Transaction list/form
   - Category management
   - Bank account management
   - Dashboard with balance charts
   - AI chat interface

2. Implement routing
3. Add authentication/authorization
4. Style with CSS framework (Bootstrap, Angular Material, etc.)
5. Add form validation
6. Implement error handling
