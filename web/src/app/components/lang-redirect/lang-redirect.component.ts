import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/i18n/language.service';

@Component({
  selector: 'app-lang-redirect',
  standalone: true,
  template: '<!-- redirecting -->',
})
export class LangRedirectComponent implements OnInit {
  constructor(private router: Router, private languageService: LanguageService) {}

  ngOnInit(): void {
    const lang = this.languageService.getPreferredLangSync();
    // Navigate replaceUrl so we don't leave an extra history entry
    this.router.navigate([`/${lang}`], { replaceUrl: true });
  }
}
