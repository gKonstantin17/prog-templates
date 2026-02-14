import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';  // 👈 ДОБАВИТЬ Router
import { LoginRequest } from '../../../core/models/login-request';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  credentials: LoginRequest = {
    login: '',
    password: ''
  };
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router  // 👈 ДОБАВИТЬ Router
  ) {}

  onSubmit(): void {
    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        if (response.user.role.name === 'Admin') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.router.navigate(['/user/dashboard']);
        }
      },
      error: () => this.error = 'Неверный логин или пароль'
    });
  }
}
