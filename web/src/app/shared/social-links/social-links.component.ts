import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

export interface SocialLink {
  label: string;
  url: string;
  icon?: string;
}

@Component({
  selector: 'app-social-links',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './social-links.component.html',
  styleUrls: ['./social-links.component.scss']
})
export class SocialLinksComponent implements OnChanges {
  @Input() links: SocialLink[] = [];

  translatedLinks: SocialLink[] = [];

  constructor(private translate: TranslateService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['links']) {
      this.updateTranslations();
    }
  }

  private updateTranslations() {
    // Map input links -> translatedLinks; if label looks like a translation key, translate it
    this.translatedLinks = (this.links && this.links.length ? this.links : this.defaultLinks()).map(l => {
      const label = l.label && l.label.indexOf('.') > -1 ? this.translate.instant(l.label) : l.label;
      return { ...l, label };
    });
  }

  private defaultLinks(): SocialLink[] {
    return [
      { label: 'CHAT.DONE.SITE', url: 'https://quasterai.com/', icon: 'fas fa-globe' },
      { label: 'CHAT.DONE.INSTAGRAM', url: 'https://www.instagram.com/quasterai', icon: 'fab fa-instagram' },
      { label: 'CHAT.DONE.LINKEDIN', url: 'https://www.linkedin.com/company/quasterai', icon: 'fab fa-linkedin' }
    ];
  }
}
