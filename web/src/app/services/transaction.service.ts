import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { BalanceRefreshService } from './balance-refresh.service';

export interface Transaction {
  id: number;
  type: 'CREDIT' | 'DEBIT';
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
  type: 'CREDIT' | 'DEBIT';
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

  constructor(
    private http: HttpClient,
    private balanceRefreshService: BalanceRefreshService
  ) {}

  create(request: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl, request).pipe(
      tap(() => this.balanceRefreshService.triggerRefresh())
    );
  }

  get(id: number): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiUrl}/${id}`);
  }

  getAll(categoryId?: number | null, bankAccountId?: number | null, month?: number | null, year?: number | null, limit?: number, offset?: number): Observable<Transaction[]> {
    let params = new HttpParams();
    
    if (categoryId !== null && categoryId !== undefined) {
      params = params.set('categoryId', categoryId.toString());
    }
    
    if (bankAccountId !== null && bankAccountId !== undefined) {
      params = params.set('bankAccountId', bankAccountId.toString());
    }
    
    if (month !== null && month !== undefined) {
      params = params.set('month', month.toString());
    }
    
    if (year !== null && year !== undefined) {
      params = params.set('year', year.toString());
    }
    
    if (limit !== undefined) {
      params = params.set('limit', limit.toString());
    }
    
    if (offset !== undefined) {
      params = params.set('offset', offset.toString());
    }
    
    return this.http.get<Transaction[]>(this.apiUrl, { params });
  }

  getYears(): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/years`);
  }

  update(id: number, request: TransactionRequest): Observable<Transaction> {
    return this.http.put<Transaction>(`${this.apiUrl}/${id}`, request).pipe(
      tap(() => this.balanceRefreshService.triggerRefresh())
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.balanceRefreshService.triggerRefresh())
    );
  }
}
