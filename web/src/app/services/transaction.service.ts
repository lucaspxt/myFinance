import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Transaction {
  id: number;
  type: 'INCOME' | 'EXPENSE';
  category: {
    id: number;
    name: string;
  };
  bankAccount: {
    id: number;
    name: string;
  };
  value: number;
  description?: string;
  createdAt: string;
}

export interface TransactionRequest {
  type: 'INCOME' | 'EXPENSE';
  categoryId: number;
  bankAccountId: number;
  value: number;
  description?: string;
  createdAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = 'http://localhost:8080/api/transactions';

  constructor(private http: HttpClient) {}

  create(request: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl, request);
  }

  get(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiUrl}/${id}`);
  }

  getAll(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(this.apiUrl);
  }

  update(id: number, request: TransactionRequest): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
