import { Component, OnInit, OnDestroy, ElementRef, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AppService, AppStatus } from '../../../services/app.service';
import { LanguageService, SupportedLanguages } from '../../../services/i18n/language.service';
import { BalanceService, TotalBalanceDTO } from '../../../services/balance.service';
import { BalanceRefreshService } from '../../../services/balance-refresh.service';
import { CurrencyFormatPipe } from '../../../shared/pipes/currency-format.pipe';

import { Subject, fromEvent, takeUntil } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, CurrencyFormatPipe],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  @Input() showSteps: boolean = false;

  protected appStatus: AppStatus | undefined;
  protected destroy$ = new Subject<void>();
  // hamburger menu state
  protected menuOpen = false;
  protected currentLang: SupportedLanguages = 'en';
  public totalBalance: TotalBalanceDTO | null = null;
  public balanceLoading: boolean = true;
  public balanceError: boolean = false;

  protected homeLink: string = '/';

  constructor(
    private appService: AppService,
    private languageService: LanguageService,
    private balanceService: BalanceService,
    private balanceRefreshService: BalanceRefreshService,
    private el: ElementRef
  ) {}

  ngOnInit(): void {
    this.appService.appStatus$
    .pipe(takeUntil(this.destroy$))
    .subscribe(status => {
      this.appStatus = status;
    });

    // Load total balance initially
    this.loadBalance();

    // Subscribe to balance refresh events triggered by transactions or chat
    this.balanceRefreshService.balanceRefresh$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.loadBalance();
      });

    // Subscribe to language changes to update local state only
    this.languageService.currentLang$
      .pipe(takeUntil(this.destroy$))
      .subscribe(lang => {
        this.currentLang = lang;
      });

    // Close menu when clicking outside the component
    fromEvent(document, 'click')
      .pipe(takeUntil(this.destroy$))
      .subscribe((event: Event) => {
        if (!this.menuOpen) return;
        const target = event.target as Node | null;
        // only close when clicking outside the menu-wrapper (button + dropdown)
        const menuWrapper: HTMLElement | null = this.el.nativeElement.querySelector('.menu-wrapper');
        if (!menuWrapper) return;
        if (!target || !menuWrapper.contains(target)) {
          this.menuOpen = false;
        }
      });
  }

  /**
   * Load total balance from the API
   */
  private loadBalance(): void {
    this.balanceLoading = true;
    this.balanceService.getTotalBalance()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (balance) => {
          this.totalBalance = balance;
          this.balanceLoading = false;
          this.balanceError = false;
        },
        error: (error) => {
          console.error('Error loading balance:', error);
          this.balanceLoading = false;
          this.balanceError = true;
          this.totalBalance = { totalBalance: 0, totalIncome: 0, totalExpense: 0 };
        }
      });
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  onLanguageChange(lang: string | null): void {
  if (!lang) return;
  this.languageService.setLanguage(lang as SupportedLanguages);
  // close menu after selecting a language
  this.menuOpen = false;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
