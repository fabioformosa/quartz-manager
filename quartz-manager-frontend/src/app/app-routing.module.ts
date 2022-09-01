import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './views/login';
import {AdminGuard, GuestGuard} from './guards';
import {NotFoundComponent} from './views/not-found';
import {ForbiddenComponent} from './views/forbidden';

import {ManagerComponent} from './views/manager';

export const routes: Routes = [
  {
    path: '',
    component: ManagerComponent,
    canActivate: [AdminGuard],
    pathMatch: 'full'
  },
  {
    path: 'manager',
    component: ManagerComponent,
    canActivate: [AdminGuard],
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [GuestGuard]
  },
  // {
  //   path: 'change-password',
  //   component: ChangePasswordComponent,
  //   canActivate: [LoginGuard]
  // },
  {
    path: '404',
    component: NotFoundComponent
  },
  {
    path: '403',
    component: ForbiddenComponent
  },
  {
    path: '**',
    redirectTo: '/404'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: []
})
export class AppRoutingModule { }
