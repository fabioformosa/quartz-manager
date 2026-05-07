import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER} from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import {JWT_OPTIONS, JwtModule} from '@auth0/angular-jwt';

// material
import {MatIconRegistry} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatChipsModule} from '@angular/material/chips';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatMenuModule} from '@angular/material/menu';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatSelectModule} from '@angular/material/select';
import {MatListModule} from '@angular/material/list';
import {MatSidenavModule} from '@angular/material/sidenav';

import {MatNativeDateModule} from '@angular/material/core';
import { NgxMatTimepickerModule, NgxMatDatetimePickerModule} from '@angular-material-components/datetime-picker';
import { NgxMatMomentModule } from '@angular-material-components/moment-adapter';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ManagerComponent } from './views/manager';
import { LoginComponent } from './views/login';
import { LoginGuard, GuestGuard, AdminGuard } from './guards';
import { NotFoundComponent } from './views/not-found';
import { AccountMenuComponent } from './components/header/account-menu/account-menu.component';

import {
  HeaderComponent,
  FooterComponent,
  GithubComponent,
  SchedulerControlComponent,
  LogsPanelComponent,
  ProgressPanelComponent,
  TriggerListComponent,
  SimpleTriggerConfigComponent
} from './components';

import {
  ApiService,
  AuthService,
  UserService,
  SchedulerService,
  ConfigService,
  getHtmlBaseUrl,
  LogsRxWebsocketService,
  ProgressRxWebsocketService,
  TriggerService
} from './services';
import { ForbiddenComponent } from './views/forbidden/forbidden.component';
import { APP_BASE_HREF } from '@angular/common';
import JobService from './services/job.service';
import {GenericErrorComponent} from './views/error/genericError.component';

export function initUserFactory(userService: UserService) {
    return () => userService.fetchLoggedUser();
}

export function jwtOptionsFactory(apiService: ApiService) {
  return {
    tokenGetter: () => {
      return apiService.getToken();
    },
    whitelistedDomains: ['localhost:8080', 'localhost:4200']
  }
}

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
    SimpleTriggerConfigComponent,
    SchedulerControlComponent,
    LogsPanelComponent,
    ProgressPanelComponent,
    ForbiddenComponent,
    GenericErrorComponent,
    TriggerListComponent
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule,
    JwtModule.forRoot({
      jwtOptionsProvider: {
        provide: JWT_OPTIONS,
        useFactory: jwtOptionsFactory,
        deps: [ApiService]
      }
    }),
    MatMenuModule,
    MatTooltipModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatToolbarModule,
    MatCardModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatDatepickerModule, MatNativeDateModule,
    NgxMatMomentModule,
    NgxMatDatetimePickerModule,
    MatSidenavModule,
    FlexLayoutModule
  ],
  providers: [
    {
      provide: APP_BASE_HREF,
      useValue: getHtmlBaseUrl()
    },
    {
      'provide': APP_INITIALIZER,
      'useFactory': initUserFactory,
      'deps': [UserService],
      'multi': true
    },
    LoginGuard,
    GuestGuard,
    AdminGuard,
    SchedulerService,
    JobService,
    TriggerService,
    ProgressRxWebsocketService,
    LogsRxWebsocketService,
    AuthService,
    ApiService,
    UserService,
    ConfigService,
    MatIconRegistry
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
