import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username = signal('');
  password = signal('');
  error = signal<string | null>(null);
  loading = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async onSubmit() {
    this.error.set(null);
    this.loading.set(true);

    try {
      await this.authService.login({
        username: this.username(),
        password: this.password()
      });

      const user = this.authService.user();
      if (user?.role === 'LOGOPED') {
        this.router.navigate(['/logoped']);
      } else {
        this.router.navigate(['/patient']);
      }
    } catch (e: any) {
      this.error.set(e?.error?.message || 'Ошибка входа. Проверьте логин и пароль.');
    } finally {
      this.loading.set(false);
    }
  }
}
