import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HttpClient, provideHttpClient, withFetch } from '@angular/common/http';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { APP_INITIALIZER } from '@angular/core';
import { LanguageService } from './services/i18n/language.service';
import { catchError, of } from 'rxjs';

// Simple class-based implementation of TranslateHttpLoader that won't use injection
export class SafeTranslateHttpLoader implements TranslateLoader {
  constructor(private http: HttpClient, private prefix: string = './assets/i18n/', private suffix: string = '.json') {}

  getTranslation(lang: string): any {
    const url = `${this.prefix}${lang}${this.suffix}`;
    
    return this.http.get(url).pipe(
      catchError((err: any) => {
        console.error(`Error loading translation for ${lang}:`, err);
        // Return empty object so the app doesn't crash
        return of({});
      })
    );
  }
}

// Factory function for creating the loader
export function createTranslateLoader(http: HttpClient) {
  // Use absolute path to assets so loader works during dev server and SSR
  return new SafeTranslateHttpLoader(http, '/assets/i18n/', '.json');
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes), 
    provideClientHydration(withEventReplay()),
    provideHttpClient(withFetch()),
    importProvidersFrom(
      TranslateModule.forRoot({
        loader: {
          provide: TranslateLoader,
          useFactory: createTranslateLoader,
          deps: [HttpClient]
        },
        defaultLanguage: 'en',
        fallbackLang: 'en',
        isolate: false
      })
    ),
    // Ensure translations are loaded before the app finishes bootstrapping
    {
      provide: APP_INITIALIZER,
      useFactory: (langService: LanguageService) => () => langService.init(),
      deps: [LanguageService],
      multi: true
    }
  ]
};
