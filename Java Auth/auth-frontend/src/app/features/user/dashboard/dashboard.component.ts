import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User } from '../../../core/models/user';
import {ProfileService} from '../../../core/services/profile';
import {AuthService} from '../../../core/services/auth';
import { Observable, of } from 'rxjs';
import { catchError, startWith } from 'rxjs/operators';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="user-layout">
      <!-- Header -->
      <div class="header">
        <div class="container">
          <nav class="navbar">
            <h2>User Panel</h2>
            <div class="user-menu">
              <span>{{ currentUser?.name }}</span>
              <button class="btn-logout" (click)="logout()">Выйти</button>
            </div>
          </nav>
        </div>
      </div>

      <!-- Main Content -->
      <div class="container main-content">
        <div class="welcome-section">
          <h1>Добро пожаловать, {{ currentUser?.name }}!</h1>
          <p class="role-badge">Ваша роль: {{ currentUser?.role?.name }}</p>
        </div>

        <div class="dashboard-grid">
          <!-- Profile Card -->
          <div class="dashboard-card">
            <div class="card-icon">👤</div>
            <h3>Мой профиль</h3>
            <p>Просмотр и редактирование личной информации</p>
            <a routerLink="/user/profile" class="btn-card">Перейти</a>
          </div>

          <!-- Status Card -->
          <div class="dashboard-card" *ngIf="status$ | async as status">
            <div class="card-icon">📊</div>
            <h3>Статус</h3>
            <p>Аутентификация: {{ status.authenticated ? '✅' : '❌' }}</p>
            <p>Админ: {{ status.isAdmin ? 'Да' : 'Нет' }}</p>
            <p>Пользователь: {{ status.isUser ? 'Да' : 'Нет' }}</p>
          </div>

          <!-- Loading indicator for status -->
          <div class="dashboard-card" *ngIf="!(status$ | async)">
            <div class="card-icon">⏳</div>
            <h3>Загрузка статуса...</h3>
            <p>Пожалуйста, подождите</p>
          </div>

          <!-- User Message Card -->
          <div class="dashboard-card" *ngIf="message$ | async as message">
            <div class="card-icon">📢</div>
            <h3>Сообщение</h3>
            <p>{{ message }}</p>
          </div>

          <!-- Loading indicator for message -->
          <div class="dashboard-card" *ngIf="!(message$ | async)">
            <div class="card-icon">⏳</div>
            <h3>Загрузка сообщения...</h3>
            <p>Пожалуйста, подождите</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .user-layout {
      min-height: 100vh;
      background: #f7fafc;
    }
    .header {
      background: white;
      box-shadow: 0 2px 5px rgba(0,0,0,0.05);
    }
    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
    }
    .navbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 0;
    }
    .user-menu {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    .btn-logout {
      padding: 0.5rem 1rem;
      background: #f56565;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
    }
    .btn-logout:hover {
      background: #c53030;
    }
    .main-content {
      padding: 2rem 20px;
    }
    .welcome-section {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 2rem;
      border-radius: 10px;
      margin-bottom: 2rem;
    }
    .role-badge {
      display: inline-block;
      padding: 0.5rem 1rem;
      background: rgba(255,255,255,0.2);
      border-radius: 5px;
      margin-top: 1rem;
    }
    .dashboard-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 1.5rem;
    }
    .dashboard-card {
      background: white;
      padding: 1.5rem;
      border-radius: 10px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.05);
    }
    .card-icon {
      font-size: 2.5rem;
      margin-bottom: 1rem;
    }
    .btn-card {
      display: inline-block;
      margin-top: 1rem;
      padding: 0.5rem 1rem;
      background: #4299e1;
      color: white;
      text-decoration: none;
      border-radius: 5px;
    }
    .btn-card:hover {
      background: #3182ce;
    }
  `]
})
export class UserDashboardComponent implements OnInit {
  currentUser: User | null = null;
  status$: Observable<any>;
  message$: Observable<string>;

  constructor(
    private profileService: ProfileService,
    private authService: AuthService
  ) {
    this.currentUser = this.authService.getCurrentUser();

    // Инициализируем Observable с начальными значениями
    this.status$ = this.profileService.getStatus().pipe(
      startWith({ authenticated: false, isAdmin: false, isUser: false }),
      catchError(() => of({ authenticated: false, isAdmin: false, isUser: false }))
    );

    this.message$ = this.profileService.getUserDashboard().pipe(
      startWith('Загрузка сообщения...'),
      catchError(() => of('Не удалось загрузить сообщение'))
    );
  }

  ngOnInit(): void {
    // Данные уже инициализированы в конструкторе
  }

  logout(): void {
    this.authService.logout();
  }
}
