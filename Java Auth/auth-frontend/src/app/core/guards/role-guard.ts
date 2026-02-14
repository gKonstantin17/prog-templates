import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import {AuthService} from '../services/auth';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: any): boolean {
    const requiredRole = route.data['role'];

    if (requiredRole === 'Admin' && this.authService.isAdmin()) {
      return true;
    }

    if (requiredRole === 'User' && this.authService.isUser()) {
      return true;
    }

    this.router.navigate(['/']);
    return false;
  }
}
