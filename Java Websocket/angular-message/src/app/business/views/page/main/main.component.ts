import {Component, OnInit} from '@angular/core';
import {AsyncPipe, CommonModule, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {User} from '../../../../oauth2/model/User';
import {SpinnerService} from '../../../../oauth2/spinner/spinner.service';
import {MessageService, MyEvent} from '../../../service/message.service';
import {KeycloakService} from '../../../../oauth2/bff/keycloak.service';
import {BackendService} from '../../../service/backend.service';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {MatTableModule} from '@angular/material/table';
import {MatInput, MatInputModule, MatLabel} from '@angular/material/input';
import {MatButton, MatButtonModule} from '@angular/material/button';


@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    NgIf,
    CommonModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,

  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements OnInit{
  user!: User; // текущий пользователь (аккаунт) - получаем только его данные - по email

  spinner: SpinnerService; // индикатор загрузки в центре экрана (при каждом HTTP запросе)

  data = ''; // данные, которые получаем из backend
  title = 'frontend';
  newEvent:MyEvent= {
    date: new Date().toISOString().slice(0, 16), // формат YYYY-MM-DDTHH:MM
    descr: ''
  };
  messages:MyEvent[] = [];
  constructor(
    private keycloakService: KeycloakService, // запросы в KC через BFF
    private backendService: BackendService, // запросы для получения бизнес-данных пользователя
    private router: Router, // навигация, общие параметры перехода на веб страницу
    private http: HttpClient, // для создания веб запросов
    private spinnerService: SpinnerService, // индикатор загрузки в центре экрана (при каждом HTTP запросе)
    private messageService: MessageService
  ) {
    // т.к. напрямую к переменной spinnerService из html обратиться нельзя (потому что private),
    // то создаем свою переменную, к которой уже обращаемся из html
    this.spinner = this.spinnerService;
  }

  ngOnInit(): void {
    this.keycloakService.requestUserProfile().subscribe({
      next: ((response: User) => {
        this.user = response;


        this.backendService.requestTestData().subscribe({
          next: ((request: any)=> {
            this.data = request.data;

              // + websocket )
              this.messageService.connect();
              this.messageService.messages$.subscribe(
                (messages) => {
                        this.messages = messages;
                      },
                (error) => {
                        console.error('Error receiving messages', error);
                      });
          }),
          error: (error => {
            console.log(error);
            this.exchangeRefreshToken();

          })
        })
      }),
      error: (error => {
        console.log(error);
        this.exchangeRefreshToken();

      })
    })


  }


  private exchangeRefreshToken() {
    this.keycloakService.exchangeRefreshToken().subscribe({
      next: (()=> {
        this.ngOnInit();
      }),
      error: (error => {
        console.log(error);
        this.redirectLoginPage();

      })
    })
  }

  private redirectLoginPage() {
    this.router.navigate(['/login']);
  }

  logoutAction() {
    this.keycloakService.logoutAction().subscribe({
      complete:(()=>{
        this.redirectLoginPage();
      })
    })
    // более универсальный способ
    // this.keycloakService.logoutAction().subscribe({
    //     next: ((response:any) => {  // он не нужон
    //     }),
    //     error: (error => {
    //       console.log(error);
    //     })
    //   });
    // this.redirectLoginPage();
  }

  sendEvent() {
    if (this.newEvent.descr.trim()) {
      this.messageService.sendEvent(this.newEvent);
      this.newEvent = {
        date: new Date().toISOString().slice(0, 16),
        descr: ''
      };
    }
  }
}
