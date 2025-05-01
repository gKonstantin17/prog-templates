import {Component, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {HttpClient, provideHttpClient} from "@angular/common/http";
import {response} from "express";
import {environment} from "../../environments/environment";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-data',
  standalone: true,
  imports: [
    NgIf
  ],
  providers: [
    HttpClient // Добавьте это
  ],
  templateUrl: './data.component.html',
  styleUrl: './data.component.css'
})
export class DataComponent implements OnInit{
  email = '';
  //data = {email:''};
  data = {followers:'',
    following:''};
  constructor(private http: HttpClient,
              private router:Router,
              private activatedRoute:ActivatedRoute) {
  }
  ngOnInit(): void {
      console.log("DataComponent");
      this.activatedRoute.queryParams.subscribe(params => {
        this.email = params['email'];
      });
    this.requestBackend();
  }

  requestBackend(): void {
    const data = {email:this.email};
    const body = JSON.stringify(data);

    this.http.post<any>(environment.BACKEND_URL+'/user/data',body,{
      headers: {
        "Content-type":"application/json"
      }
    })
      .subscribe({
        next: ((response: any) => {
          console.log(response);
          console.log(response.data);

          this.data.followers = response.followers;
          this.data.following = response.following;
        }),
        error: (error => {
          console.log(error)
        })
      });
  }

  logout() {
    this.router.navigate(['/logout']);
  }
}
