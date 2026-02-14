import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User } from '../../../core/models/user';
import {AdminService} from '../../../core/services/admin';
import {AuthService} from '../../../core/services/auth';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="admin-layout">
      <!-- Sidebar -->
      <div class="sidebar">
        <div class="sidebar-header">
          <h3>Admin Panel</h3>
          <p>{{ currentUser?.name }}</p>
        </div>
        <nav class="sidebar-nav">
          <a routerLink="/admin/dashboard" routerLinkActive="active">
            📊 Дашборд
          </a>
          <a routerLink="/admin/users">
            👥 Пользователи
          </a>
          <a href="#" (click)="logout($event)">
            🚪 Выйти
          </a>
        </nav>
      </div>

      <!-- Main Content -->
      <div class="main-content">
        <div class="header">
          <h1>Панель администратора</h1>
        </div>

        <!-- Stats Cards -->
        <div class="stats-grid">
          <div class="stat-card" *ngIf="stats">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
              <h3>Всего пользователей</h3>
              <div class="stat-number">{{ stats.totalUsers }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">✅</div>
            <div class="stat-info">
              <h3>Активных</h3>
              <div class="stat-number">{{ stats?.activeUsers || 0 }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">📝</div>
            <div class="stat-info">
              <h3>Постов</h3>
              <div class="stat-number">{{ stats?.totalPosts || 0 }}</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">👑</div>
            <div class="stat-info">
              <h3>Ваша роль</h3>
              <div class="stat-role">ADMIN</div>
            </div>
          </div>
        </div>

        <!-- Admin Message -->
        <div class="welcome-card">
          <h2>{{ dashboardMessage }}</h2>
          <p>Добро пожаловать в панель администратора. Здесь вы можете управлять пользователями и настройками системы.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .admin-layout {
      display: flex;
      min-height: 100vh;
    }
    .sidebar {
      width: 260px;
      background: #2d3748;
      color: white;
    }
    .sidebar-header {
      padding: 1.5rem;
      border-bottom: 1px solid #4a5568;
    }
    .sidebar-header h3 {
      margin: 0;
      color: #e2e8f0;
    }
    .sidebar-header p {
      margin: 0.5rem 0 0;
      color: #a0aec0;
      font-size: 0.875rem;
    }
    .sidebar-nav {
      padding: 1.5rem 0;
    }
    .sidebar-nav a {
      display: block;
      padding: 0.75rem 1.5rem;
      color: #e2e8f0;
      text-decoration: none;
      transition: all 0.3s;
    }
    .sidebar-nav a:hover {
      background: #4a5568;
    }
    .sidebar-nav a.active {
      background: #4299e1;
    }
    .main-content {
      flex: 1;
      background: #f7fafc;
      padding: 2rem;
    }
    .header {
      margin-bottom: 2rem;
    }
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1.5rem;
      margin-bottom: 2rem;
    }
    .stat-card {
      background: white;
      padding: 1.5rem;
      border-radius: 10px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.05);
      display: flex;
      align-items: center;
    }
    .stat-icon {
      font-size: 2.5rem;
      margin-right: 1rem;
    }
    .stat-info h3 {
      margin: 0;
      font-size: 0.875rem;
      color: #718096;
    }
    .stat-number {
      font-size: 1.875rem;
      font-weight: bold;
      color: #2d3748;
    }
    .stat-role {
      font-size: 1.5rem;
      font-weight: bold;
      color: #48bb78;
    }
    .welcome-card {
      background: white;
      padding: 2rem;
      border-radius: 10px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.05);
    }
  `]
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  stats: any = null;
  dashboardMessage = 'Загрузка...';

  constructor(
    private adminService: AdminService,
    private authService: AuthService
  ) {
    this.currentUser = this.authService.getCurrentUser();
  }

  ngOnInit(): void {
    this.loadDashboard();
    this.loadStats();
  }

  loadDashboard(): void {
    this.adminService.getDashboard().subscribe({
      next: (data) => {
        this.dashboardMessage = data.message;
      }
    });
  }

  loadStats(): void {
    this.adminService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
      }
    });
  }

  logout(event: Event): void {
    event.preventDefault();
    this.authService.logout();
  }
}
