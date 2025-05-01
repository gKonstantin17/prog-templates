import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../model/User';
import {enviroment} from '../../../enviroments/enviroment';


@Injectable({
  providedIn: 'root'
})

export class KeycloakService {

  constructor(private http: HttpClient) {
  }


  // выход из системы
  logoutAction(): Observable<any> { //
    // просто вызываем адрес и ничего не возвращаем
    return this.http.get(enviroment.BFF_URI + '/bff/logout');
  }


  // получаем новые токены с помощью старого Refresh Token (из кука)
  exchangeRefreshToken(): Observable<any> {
    return this.http.get(enviroment.BFF_URI + '/bff/exchange');
  }

  // запрос данных пользователя (профайл)
  requestUserProfile(): Observable<User> {
    return this.http.get<User>(enviroment.BFF_URI + '/bff/profile');
  }


}
