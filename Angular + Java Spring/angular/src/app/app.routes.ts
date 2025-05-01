import { Routes } from '@angular/router';
import {DataComponent} from './data/data.component';
import {LoginComponent} from './login/login.component';

export const routes: Routes = [
  {path:'',component:LoginComponent},
  {path:'data',component:DataComponent},
  {path:'logout',component:LoginComponent},
];
