import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { HeaderComponent } from '../../components/layout/header/header.component';

@Component({
  selector: 'app-terms',
  standalone: true,
  imports: [CommonModule, TranslateModule, HeaderComponent],
  templateUrl: './terms.component.html'
})
export class TermsComponent {}
