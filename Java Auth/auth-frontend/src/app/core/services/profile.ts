import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = 'https://security.cloudpub.ru/api/profile';

  constructor(private http: HttpClient) {}

  getCurrentUserProfile(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`);
  }

  getUserDashboard(): Observable<any> {
    return this.http.get(`${this.apiUrl}/dashboard`);
  }

  getStatus(): Observable<any> {
    return this.http.get(`${this.apiUrl}/status`);
  }
}
