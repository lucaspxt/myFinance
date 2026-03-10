import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-repeat-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './repeat-button.component.html',
  styleUrls: ['./repeat-button.component.scss']
})
export class RepeatButtonComponent {
  @Input() label: string = 'Repeat';
  // Accept a callable passed via ngComponentOutlet inputs
  @Input() onRepeat?: () => void;

  handleClick() {
    if (this.onRepeat) {
      try { this.onRepeat(); } catch (e) { console.warn('onRepeat threw', e); }
    } else {
      console.debug('Repeat clicked but no handler provided');
    }
  }
}
