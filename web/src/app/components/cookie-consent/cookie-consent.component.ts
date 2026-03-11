import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-cookie-consent',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './cookie-consent.component.html',
  styleUrls: ['./cookie-consent.component.scss']
})
export class CookieConsentComponent implements OnInit {
  visible = false; // default hidden to avoid SSR flicker

  private storageKey = 'quaster_cookie_consent';

  constructor(private translate: TranslateService) {}

  ngOnInit(): void {
    // Only access localStorage in browser environment
    try {
      if (typeof window !== 'undefined' && window?.localStorage) {
        // small debounce to avoid quick flip if other scripts set the key on startup
        setTimeout(() => {
          try {
            const val = localStorage.getItem(this.storageKey);
            this.visible = !val;
          } catch (e) {
            this.visible = false;
          }
        }, 50);
      } else {
        this.visible = false;
      }
    } catch (e) {
      this.visible = false;
    }
  }

  accept(): void {
  try { localStorage.setItem(this.storageKey, 'accepted'); } catch (e) {}
    this.visible = false;
  }

  decline(): void {
    try { localStorage.setItem(this.storageKey, 'declined'); } catch (e) {}
    this.visible = false;
  }
}
