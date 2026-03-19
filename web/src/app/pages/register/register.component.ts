import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  name: string = '';
  email: string = '';
  password: string = '';
  isLoading: boolean = false;
  errorKey: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    if (!this.name || !this.email || !this.password) return;
    this.isLoading = true;
    this.errorKey = '';
    this.authService.register(this.name, this.email, this.password).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => {
        const msg = err?.error?.error;
        this.errorKey = msg === 'Email already in use'
          ? 'AUTH.REGISTER.ERROR_EMAIL_EXISTS'
          : 'AUTH.REGISTER.ERROR';
        this.isLoading = false;
      }
    });
  }
}
