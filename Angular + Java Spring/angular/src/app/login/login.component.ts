import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
  email = '';

  constructor(private router:Router) {
  }
  ngOnInit(): void {
    this.email = "example@mail.com"
  }

  openDataPage() {
    this.router.navigate(['/data'],{queryParams:{email:this.email}})
  }
}
