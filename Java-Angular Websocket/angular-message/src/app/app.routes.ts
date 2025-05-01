import { Routes } from '@angular/router';
import {LoginComponent} from './oauth2/login/login.component';
import {MainComponent} from './business/views/page/main/main.component';

export const routes: Routes = [
  {path:'', component:LoginComponent},
  {path:'main', component:MainComponent},
  {path:'index', component:LoginComponent},
  {path:'login', component:LoginComponent}
];
