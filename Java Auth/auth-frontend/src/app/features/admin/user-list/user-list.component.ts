import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User } from '../../../core/models/user';
import {UserService} from '../../../core/services/user';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="user-list-container">
      <h2>Управление пользователями</h2>

      <table class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Логин</th>
            <th>Роль</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let user of users">
            <td>{{ user.id }}</td>
            <td>{{ user.name }}</td>
            <td>{{ user.login }}</td>
            <td>
              <span [class]="'badge ' + (user.role.name === 'Admin' ? 'badge-admin' : 'badge-user')">
                {{ user.role }}
              </span>
            </td>
            <td>
              <button class="btn-edit">Редактировать</button>
              <button class="btn-delete">Удалить</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .user-list-container {
      padding: 2rem;
    }
    .user-table {
      width: 100%;
      border-collapse: collapse;
      background: white;
      border-radius: 10px;
      overflow: hidden;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    }
    th, td {
      padding: 1rem;
      text-align: left;
      border-bottom: 1px solid #e2e8f0;
    }
    th {
      background: #f7fafc;
      font-weight: 600;
    }
    .badge {
      padding: 0.25rem 0.75rem;
      border-radius: 20px;
      font-size: 0.875rem;
      font-weight: 500;
    }
    .badge-admin {
      background: #feb2b2;
      color: #c53030;
    }
    .badge-user {
      background: #bee3f8;
      color: #2c5282;
    }
    .btn-edit, .btn-delete {
      padding: 0.25rem 0.75rem;
      margin: 0 0.25rem;
      border: none;
      border-radius: 5px;
      cursor: pointer;
    }
    .btn-edit {
      background: #4299e1;
      color: white;
    }
    .btn-delete {
      background: #f56565;
      color: white;
    }
  `]
})
export class UserListComponent implements OnInit {
  users: User[] = [];

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Error loading users', err)
    });
  }
}
