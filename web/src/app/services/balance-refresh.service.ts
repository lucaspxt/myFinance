import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * Service to emit events when balance data should be refreshed.
 * Used to trigger balance reload after transactions are created/updated/deleted.
 */
@Injectable({
  providedIn: 'root'
})
export class BalanceRefreshService {
  private refreshBalanceSubject = new Subject<void>();

  /**
   * Observable that components can subscribe to for balance refresh events
   */
  public balanceRefresh$ = this.refreshBalanceSubject.asObservable();

  /**
   * Triggers a balance refresh event
   */
  public triggerRefresh(): void {
    this.refreshBalanceSubject.next();
  }
}
