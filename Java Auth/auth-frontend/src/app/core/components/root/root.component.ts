import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-root',
  standalone: true,
  template: '<div>Загрузка...</div>'
})
export class RootComponent implements OnInit {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async ngOnInit() {
    console.log('RootComponent: проверка сессии');
    const user = await this.authService.checkSession();

    if (user) {
      console.log('RootComponent: пользователь авторизован', user);

      const roleName = user.role?.name;

      console.log('RootComponent: роль пользователя', roleName);

      setTimeout(async () => {
        if (roleName === 'Admin') {
          await this.router.navigate(['/admin/dashboard'], { replaceUrl: true });
        } else {
          await this.router.navigate(['/user/dashboard'], { replaceUrl: true });
        }
      }, 100);
    } else {
      console.log('RootComponent: нет авторизации');
      setTimeout(async () => {
        await this.router.navigate(['/login'], { replaceUrl: true });
      }, 100);
    }
  }
}
