import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideClientHydration} from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {CookiesInterceptorService} from './oauth2/interceptor/cookies-interceptor.service';
import {SpinnerInterceptor} from './oauth2/interceptor/spinner-inceptor.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),           // Маршрутизация
    //provideClientHydration(),        // Гидратация клиента
    provideHttpClient(withInterceptorsFromDi()),
    {provide: HTTP_INTERCEPTORS, useClass: CookiesInterceptorService, multi: true},// Регистрируем интерсептор
    {provide: HTTP_INTERCEPTORS, useClass: SpinnerInterceptor, multi: true},
  ]
};

