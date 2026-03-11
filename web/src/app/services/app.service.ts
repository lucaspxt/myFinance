import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';

export enum AppStatus {
  CHAT = 'chat',
  CONTACT = 'contact',
  DONE = 'done',
  ERROR = 'error'
}

@Injectable({
  providedIn: 'root'
})
export class AppService {
  private _appStatus$ = new BehaviorSubject<AppStatus>(AppStatus.CHAT);
  public appStatus$ = this._appStatus$.asObservable();

  constructor() { }

  setAppStatus(status: AppStatus): void {
    this._appStatus$.next(status);
  }
}
