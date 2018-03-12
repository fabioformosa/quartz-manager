import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER} from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';
// material
import {
  MatButtonModule,
  MatMenuModule,
  MatIconModule,
  MatToolbarModule,
  MatTooltipModule,
  MatCardModule,
  MatChipsModule,
  MatInputModule,
  MatIconRegistry,
  MatProgressSpinnerModule,
  MatProgressBarModule,
} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ManagerComponent } from './manager';
import { LoginComponent } from './login';
import { LoginGuard, GuestGuard, AdminGuard } from './guard';
import { NotFoundComponent } from './not-found';
import { AccountMenuComponent } from './component/header/account-menu/account-menu.component';

import {
  HeaderComponent,
  FooterComponent,
  GithubComponent,
  SchedulerConfigComponent,
  SchedulerControlComponent,
  LogsPanelComponent,
  ProgressPanelComponent
} from './component';

import {
  ApiService,
  AuthService,
  UserService,
  FooService,
  SchedulerService,
  ConfigService,
  ProgressWebsocketService,
  LogsWebsocketService
} from './service';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { ForbiddenComponent } from './forbidden/forbidden.component';

export function initUserFactory(userService: UserService) {
    return () => userService.jsessionInitUser();
}

// const stompConfig: StompConfig = {
//   // Which server?
//   url: 'ws://localhost:8080/quartz-manager/progress',

//   // Headers
//   // Typical keys: login, passcode, host
//   headers: {
//     login: 'admin',
//     passcode: 'admin'
//   },

//   // How often to heartbeat?
//   // Interval in milliseconds, set to 0 to disable
//   heartbeat_in: 0, // Typical value 0 - disabled
//   heartbeat_out: 20000, // Typical value 20000 - every 20 seconds
//   // Wait in milliseconds before attempting auto reconnect
//   // Set to 0 to disable
//   // Typical value 5000 (5 seconds)
//   reconnect_delay: 5000,

//   // Will log diagnostics on console
//   debug: true
// };

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    ManagerComponent,
    GithubComponent,
    LoginComponent,
    NotFoundComponent,
    AccountMenuComponent,
    SchedulerConfigComponent,
    SchedulerControlComponent,
    LogsPanelComponent,
    ProgressPanelComponent,
    ChangePasswordComponent,
    ForbiddenComponent
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    HttpClientModule,
    AppRoutingModule,
    MatMenuModule,
    MatTooltipModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatInputModule,
    MatToolbarModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    FlexLayoutModule
  ],
  providers: [
    LoginGuard,
    GuestGuard,
    AdminGuard,
    FooService,
    SchedulerService,
    ProgressWebsocketService,
    LogsWebsocketService,
    AuthService,
    ApiService,
    UserService,
    ConfigService,
    MatIconRegistry,
    {
      'provide': APP_INITIALIZER,
      'useFactory': initUserFactory,
      'deps': [UserService],
      'multi': true
    }
    // StompService,
    // ServerSocket
    // {
    //   provide: StompConfig,
    //   useValue: stompConfig
    // }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
