import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth-guard';
import { RoleGuard } from './core/guards/role-guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./core/components/root/root.component').then(m => m.RootComponent)
      }
    ]
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'admin/dashboard',
    loadComponent: () => import('./features/admin/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'Admin' }
  },
  {
    path: 'admin/users',
    loadComponent: () => import('./features/admin/user-list/user-list.component').then(m => m.UserListComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'Admin' }
  },
  {
    path: 'user/dashboard',
    loadComponent: () => import('./features/user/dashboard/dashboard.component').then(m => m.UserDashboardComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'User' }
  },
  {
    path: 'user/profile',
    loadComponent: () => import('./features/user/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [AuthGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
