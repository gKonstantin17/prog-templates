import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { 
    path: 'logoped', 
    loadComponent: () => import('./components/logoped-dashboard/logoped-dashboard.component').then(m => m.LogopedDashboardComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'patient', 
    loadComponent: () => import('./components/patient-dashboard/patient-dashboard.component').then(m => m.PatientDashboardComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'room/:id', 
    loadComponent: () => import('./components/video-room/video-room.component').then(m => m.VideoRoomComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: 'login' }
];
