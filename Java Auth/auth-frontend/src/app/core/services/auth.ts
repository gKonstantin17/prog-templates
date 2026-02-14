import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, lastValueFrom } from 'rxjs';
import { Router } from '@angular/router';
import { AuthResponse } from '../models/auth-response';
import { User } from '../models/user';
import { LoginRequest } from '../models/login-request';
import { RegisterRequest } from '../models/register-request';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'https://security.cloudpub.ru/api/auth';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials, { withCredentials: true }).pipe(
      tap(response => {
        this.currentUserSubject.next(response.user);
      })
    );
  }

  refreshToken(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {}, { withCredentials: true }).pipe(
      tap(response => {
        this.currentUserSubject.next(response.user);
      })
    );
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => {
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
      },
      error: () => {
        this.currentUserSubject.next(null);
        this.router.navigate(['/login']);
      }
    });
  }

  isAdmin(): boolean {
    return this.currentUserSubject.value?.role?.name === 'Admin';
  }
  isUser(): boolean {
    return this.currentUserSubject.value?.role?.name === 'User';
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  register(userData: RegisterRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register`, userData);
  }

  async checkSession(): Promise<User | null> {
    try {
      const response = await lastValueFrom(
        this.http.get<{ user: User }>(`${this.apiUrl}/check`, { withCredentials: true })
      );

      if (response?.user) {
        this.currentUserSubject.next(response.user);
        return response.user;
      } else {
        this.currentUserSubject.next(null);
        return null;
      }
    } catch (error) {
      this.currentUserSubject.next(null);
      return null;
    }
  }
}
