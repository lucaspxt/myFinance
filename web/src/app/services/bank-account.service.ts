import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BankAccount {
  id: number;
  name: string;
  defaultAccount: boolean;
  archived: boolean;
  balance?: number;
}

export interface BankAccountRequest {
  name: string;
  defaultAccount: boolean;
  archived?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class BankAccountService {
  private apiUrl = 'http://localhost:8080/api/bank-accounts';

  constructor(private http: HttpClient) {}

  create(request: BankAccountRequest): Observable<BankAccount> {
    return this.http.post<BankAccount>(this.apiUrl, request);
  }

  get(id: number): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.apiUrl}/${id}`);
  }

  getAll(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(this.apiUrl);
  }

  update(id: number, request: BankAccountRequest): Observable<BankAccount> {
    return this.http.put<BankAccount>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
