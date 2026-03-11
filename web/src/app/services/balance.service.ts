import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TotalBalanceDTO {
  totalBalance: number;
  totalIncome: number;
  totalExpense: number;
}

export interface BankAccountBalanceDTO {
  bankAccountId: number;
  bankAccountName: string;
  balance: number;
}

export interface CategoryBalanceDTO {
  categoryId: number;
  categoryName: string;
  totalExpense: number;
  transactionCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class BalanceService {
  private apiUrl = 'http://localhost:8080/api/balance';

  constructor(private http: HttpClient) {}

  getTotalBalance(): Observable<TotalBalanceDTO> {
    return this.http.get<TotalBalanceDTO>(`${this.apiUrl}/total`);
  }

  getBalanceByBankAccount(): Observable<BankAccountBalanceDTO[]> {
    return this.http.get<BankAccountBalanceDTO[]>(`${this.apiUrl}/by-bank-account`);
  }

  getBalanceByCategory(): Observable<CategoryBalanceDTO[]> {
    return this.http.get<CategoryBalanceDTO[]>(`${this.apiUrl}/by-category`);
  }
}
