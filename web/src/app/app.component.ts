import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppService, AppStatus } from './services/app.service';
import { Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CookieConsentComponent } from './components/cookie-consent/cookie-consent.component';
import { LanguageService } from './services/i18n/language.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, TranslateModule, CookieConsentComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  standalone: true
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'lead-web';
  protected appStatus: AppStatus | undefined;
  protected destroy$ = new Subject<void>();

  constructor(private appService: AppService, private languageService: LanguageService, private translate: TranslateService) {}

  ngOnInit(): void {
    this.appService.appStatus$
    .pipe(takeUntil(this.destroy$))
    .subscribe(status => {
      this.appStatus = status;
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
