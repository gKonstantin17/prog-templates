import { Component, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SessionService } from '../../services/session.service';
import { Session } from '../../models/session';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './patient-dashboard.component.html',
  styleUrls: ['./patient-dashboard.component.scss']
})
export class PatientDashboardComponent implements OnInit {
  sessions = signal<Session[]>([]);
  loading = signal(false);
  userName = signal('');

  constructor(
    private authService: AuthService,
    private sessionService: SessionService,
    private router: Router,
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

  isWaiting(session: Session): boolean {
    const now = new Date();
    const startTime = new Date(session.startTime);
    const windowStart = new Date(startTime.getTime() - 15 * 60 * 1000);
    return now < windowStart;
  }

  isEnded(session: Session): boolean {
    const now = new Date();
    const endTime = new Date(session.endTime);
    return now > endTime;
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
