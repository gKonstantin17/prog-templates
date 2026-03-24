import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { firstValueFrom } from 'rxjs';

export interface LoginCredentials {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSignal = signal<User | null>(null);
  
  public user = computed(() => this.currentUserSignal());
  public isLoggedIn = computed(() => this.currentUserSignal() !== null);
  public isLogoped = computed(() => this.currentUserSignal()?.role === 'LOGOPED');
  public isPatient = computed(() => this.currentUserSignal()?.role === 'PATIENT');

  constructor(private http: HttpClient) {
    this.checkAuth();
  }

  async login(credentials: LoginCredentials): Promise<User> {
    const response = await firstValueFrom(
      this.http.post<User>('/api/auth/login', credentials, { withCredentials: true })
    );
    this.currentUserSignal.set(response);
    return response;
  }

  async logout(): Promise<void> {
    await firstValueFrom(
      this.http.post('/api/auth/logout', {}, { withCredentials: true })
    );
    this.currentUserSignal.set(null);
  }

  async checkAuth(): Promise<void> {
    try {
      const user = await firstValueFrom(
        this.http.get<User>('/api/auth/me', { withCredentials: true })
      );
      this.currentUserSignal.set(user);
    } catch {
      this.currentUserSignal.set(null);
    }
  }

  /**
   * Возвращает текущего пользователя (если авторизован)
   */
  async getCurrentUser(): Promise<User> {
    const user = await firstValueFrom(
      this.http.get<User>('/api/auth/me', { withCredentials: true })
    );
    this.currentUserSignal.set(user);
    return user;
  }
}
