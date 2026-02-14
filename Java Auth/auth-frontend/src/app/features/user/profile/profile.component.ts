import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../../../core/models/user';
import {ProfileService} from '../../../core/services/profile';
import {AuthService} from '../../../core/services/auth';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="profile-container">
      <h2>Мой профиль</h2>

      <div class="profile-card" *ngIf="profile">
        <div class="profile-header">
          <div class="profile-avatar">
            {{ profile.user.name.charAt(0) }}
          </div>
          <div class="profile-title">
            <h3>{{ profile.user.name }}</h3>
            <p>@{{ profile.user.login }}</p>
          </div>
        </div>

        <div class="profile-info">
          <div class="info-row">
            <span class="info-label">Роль:</span>
            <span class="info-value badge">{{ profile.user.role }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">ID:</span>
            <span class="info-value">{{ profile.user.id }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Статус:</span>
            <span class="info-value">{{ profile.authenticated ? 'Активен' : 'Неактивен' }}</span>
          </div>
        </div>

        <div class="profile-message">
          {{ profile.message }}
        </div>
      </div>
    </div>
  `,
  styles: [`
    .profile-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 2rem;
    }
    .profile-card {
      background: white;
      border-radius: 10px;
      padding: 2rem;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    }
    .profile-header {
      display: flex;
      align-items: center;
      gap: 1.5rem;
      margin-bottom: 2rem;
      padding-bottom: 1.5rem;
      border-bottom: 1px solid #e2e8f0;
    }
    .profile-avatar {
      width: 80px;
      height: 80px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 2rem;
      font-weight: bold;
    }
    .profile-title h3 {
      margin: 0;
      color: #2d3748;
    }
    .profile-title p {
      margin: 0.5rem 0 0;
      color: #718096;
    }
    .profile-info {
      margin-bottom: 1.5rem;
    }
    .info-row {
      display: flex;
      padding: 0.75rem 0;
      border-bottom: 1px solid #e2e8f0;
    }
    .info-label {
      width: 120px;
      color: #718096;
      font-weight: 500;
    }
    .info-value {
      color: #2d3748;
    }
    .badge {
      padding: 0.25rem 0.75rem;
      background: #48bb78;
      color: white;
      border-radius: 20px;
      font-size: 0.875rem;
    }
    .profile-message {
      padding: 1rem;
      background: #ebf8ff;
      color: #2c5282;
      border-radius: 5px;
      text-align: center;
    }
  `]
})
export class ProfileComponent implements OnInit {
  profile: any = null;

  constructor(
    private profileService: ProfileService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.profileService.getCurrentUserProfile().subscribe({
      next: (data) => this.profile = data,
      error: (err) => console.error('Error loading profile', err)
    });
  }
}
