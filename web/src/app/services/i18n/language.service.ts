import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, of, firstValueFrom } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';

export type SupportedLanguages = 'en' | 'pt' | 'es' | 'it' | 'de' | 'fr';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private _currentLang$ = new BehaviorSubject<SupportedLanguages>('en');
  public currentLang$ = this._currentLang$.asObservable();

  constructor(private translateService: TranslateService, private http: HttpClient) {
    // Do not perform language initialization in the constructor. Initialization is
    // handled by the `init()` method which is intended to be invoked by an
    // APP_INITIALIZER so the app can wait for translations to load before
    // rendering. This avoids showing translation keys briefly on first paint.
  }

  /**
   * Initialize language subsystem and ensure the chosen language translations
   * are loaded before the application continues bootstrapping.
   * Returns a Promise that resolves once translations for the selected
   * language are available (or rejects/resolves gracefully on error).
   */
  public async init(): Promise<void> {
    try {
      // Available languages and default
  this.translateService.addLangs(['en', 'pt', 'es', 'it', 'de', 'fr']);
  const defaultLang: SupportedLanguages = 'en';

  // Determine preferred language: localStorage > browser > default

  let savedLang: SupportedLanguages | null = null;
  try { savedLang = localStorage.getItem('preferred_language') as SupportedLanguages; } catch (e) { /* ignore */ }

  const browserLang = this.translateService.getBrowserLang();
  const langToUse = savedLang || (browserLang && ['en','pt','es','it','de','fr'].includes(browserLang) ? browserLang as SupportedLanguages : defaultLang);

  // Set default only after we've checked localStorage/browser so we don't
  // prematurely force 'en' when the user already has a saved preference.
  this.translateService.setDefaultLang(defaultLang);

      // Use the language via TranslateService and wait for the loader to finish
      // loading translations for that language. firstValueFrom will resolve when
      // the observable completes/emits the translations.
      try {
        await firstValueFrom(this.translateService.use(langToUse));
      } catch (e) {
        // If loading fails, fall back to default language but don't block
        console.warn('Could not load translations for', langToUse, e);
        try { await firstValueFrom(this.translateService.use(defaultLang)); } catch { /* ignore */ }
      }

      // Update BehaviorSubject so other parts of the app know the language
      this._currentLang$.next(this.translateService.currentLang as SupportedLanguages || defaultLang);

      // Preload other translations in background (do not await)
      ['en','pt','es','it','de','fr'].forEach((l) => {
        if (l !== this.getCurrentLang()) this.preloadTranslation(l as SupportedLanguages);
      });
    } catch (error) {
      console.error('LanguageService.init() error:', error);
    }
  }

  /**
   * Compute preferred language using the same priority used during init,
   * but synchronously so route resolution can use it before navigation.
   */
  // URL-based language resolution removed. Language selection uses localStorage > browser > default.

  private preloadTranslation(lang: SupportedLanguages) {
    const url = `/assets/i18n/${lang}.json`;
    this.http.get(url).pipe(
      catchError(err => {
        console.warn(`Could not preload translation ${lang}:`, err);
        return of(null);
      })
    ).subscribe((data: any) => {
      if (data) {
        try {
          this.translateService.setTranslation(lang, data, true);
        } catch (e) {
          console.warn('Error setting translation:', e);
        }
      }
    });
  }

  initLanguage() {
    try {
  // Initializing language preferences (log suppressed)
      
      // Get browser language if available or use default
  const browserLang = this.translateService.getBrowserLang();
      const defaultLang: SupportedLanguages = 'en';
      let savedLang: SupportedLanguages | null = null;
      
      try {
        savedLang = localStorage.getItem('preferred_language') as SupportedLanguages;
      } catch (e) {
        console.warn('Could not access localStorage:', e);
      }
      
  // Use saved language (localStorage), then browser language, then default
  const langToUse = savedLang || (browserLang && ['en', 'pt', 'es', 'it', 'de', 'fr'].includes(browserLang) ? browserLang as SupportedLanguages : defaultLang);
      
  // Using language: (suppressed)
      this.setLanguage(langToUse);
    } catch (error) {
      console.error('Error in initLanguage:', error);
      // Set default language as fallback
      this.setLanguage('en');
    }
  }

  setLanguage(lang: SupportedLanguages) {
  // Setting language to: (suppressed)
    
    try {
      // Save to localStorage
      try {
        localStorage.setItem('preferred_language', lang);
      } catch (e) {
        console.warn('Could not save to localStorage:', e);
      }
      
  // Update translation service
  console.debug('LanguageService.setLanguage() setting language ->', lang);
  this.translateService.use(lang);
      
      // Update BehaviorSubject
      this._currentLang$.next(lang);
      
  // Language set successfully (suppressed)
    } catch (error) {
      console.error('Error setting language:', error);
    }
  }

  getCurrentLang(): SupportedLanguages {
    return this._currentLang$.getValue();
  }
}
