import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SessionService, CreateSessionRequest } from '../../services/session.service';
import { Session } from '../../models/session';
import { User } from '../../models/user';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatDialogModule, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CreateSessionDialogComponent } from './create-session-dialog.component';

@Component({
  selector: 'app-logoped-dashboard',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatListModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './logoped-dashboard.component.html',
  styleUrls: ['./logoped-dashboard.component.scss']
})
export class LogopedDashboardComponent implements OnInit {
  sessions = signal<Session[]>([]);
  loading = signal(false);
  userName = signal('');

  constructor(
    private authService: AuthService,
    private sessionService: SessionService,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    const user = this.authService.user();
    this.userName.set(user?.fullName || user?.username || '');
    this.loadSessions();
  }

  async loadSessions() {
    this.loading.set(true);
    try {
      const sessions = await this.sessionService.getSessions();
      this.sessions.set(sessions);
    } catch (e: any) {
      this.snackBar.open('Ошибка загрузки занятий', 'Закрыть', { duration: 3000 });
    } finally {
      this.loading.set(false);
    }
  }

  async logout() {
    await this.authService.logout();
    this.router.navigate(['/login']);
  }

  async joinSession(sessionId: number) {
    try {
      const tokenResponse = await this.sessionService.getJoinToken(sessionId);
      this.router.navigate(['/room', sessionId], {
        queryParams: {
          url: tokenResponse.url,
          token: tokenResponse.token,
          room: tokenResponse.roomName
        }
      });
    } catch (e: any) {
      this.snackBar.open('Ошибка подключения к комнате', 'Закрыть', { duration: 3000 });
    }
  }

  async completeSession(sessionId: number) {
    if (!confirm('Завершить это занятие?')) return;

    try {
      await this.sessionService.completeSession(sessionId);
      this.snackBar.open('Занятие завершено', 'Закрыть', { duration: 3000 });
      this.loadSessions();
    } catch (e: any) {
      this.snackBar.open('Ошибка завершения занятия', 'Закрыть', { duration: 3000 });
    }
  }

  openCreateDialog() {
    const dialogRef = this.dialog.open(CreateSessionDialogComponent, {
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(async (result) => {
      if (result) {
        try {
          await this.sessionService.createSession(result);
          this.snackBar.open('Занятие создано', 'Закрыть', { duration: 3000 });
          this.loadSessions();
        } catch (e: any) {
          this.snackBar.open('Ошибка создания занятия', 'Закрыть', { duration: 3000 });
        }
      }
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('ru-RU', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
