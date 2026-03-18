import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Session } from '../models/session';
import { firstValueFrom } from 'rxjs';

export interface CreateSessionRequest {
  title: string;
  startTime: string;
  endTime: string;
  patientIds: number[];
}

export interface JoinTokenResponse {
  url: string;      // Домен Jitsi (например, meet.jit.si или localhost:8000)
  token: string;    // JWT токен для авторизации
  roomName: string; // Имя комнаты
}

export interface SessionAccess {
  sessionId: number;
  status: 'WAITING' | 'ALLOWED' | 'ENDED';
  canJoin: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  constructor(private http: HttpClient) {}

  async getSessions(): Promise<Session[]> {
    return firstValueFrom(this.http.get<Session[]>('/api/sessions', { withCredentials: true }));
  }

  async createSession(request: CreateSessionRequest): Promise<Session> {
    return firstValueFrom(
      this.http.post<Session>('/api/sessions', request, { withCredentials: true })
    );
  }

  async getSession(id: number): Promise<Session> {
    return firstValueFrom(this.http.get<Session>(`/api/sessions/${id}`, { withCredentials: true }));
  }

  async getSessionStatus(id: number): Promise<SessionAccess> {
    return firstValueFrom(
      this.http.get<SessionAccess>(`/api/sessions/${id}/status`, { withCredentials: true })
    );
  }

  async getJoinToken(id: number): Promise<JoinTokenResponse> {
    return firstValueFrom(
      this.http.post<JoinTokenResponse>(`/api/sessions/${id}/join-token`, {}, { withCredentials: true })
    );
  }

  async completeSession(id: number): Promise<Session> {
    return firstValueFrom(
      this.http.post<Session>(`/api/sessions/${id}/complete`, {}, { withCredentials: true })
    );
  }
}
