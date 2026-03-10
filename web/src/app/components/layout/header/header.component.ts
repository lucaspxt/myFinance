import { Component, OnInit, OnDestroy, ElementRef, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AppService, AppStatus } from '../../../services/app.service';
import { LanguageService, SupportedLanguages } from '../../../services/i18n/language.service';

import { Subject, fromEvent, takeUntil } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule],
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
  // Você pode adicionar propriedades e métodos do componente aqui

  protected homeLink: string = '/';

  constructor(private appService: AppService, private languageService: LanguageService, private el: ElementRef) {}

  ngOnInit(): void {
    this.appService.appStatus$
    .pipe(takeUntil(this.destroy$))
    .subscribe(status => {
      this.appStatus = status;
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
