import { TestBed, async } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { ManagerComponent } from './views/manager';
import { LoginComponent } from './views/login';
import { MockApiService } from './services/mocks/api.service.mock';

import { LoginGuard } from './guards';
import { NotFoundComponent } from './views/not-found';
import {
  FooterComponent,
  GithubComponent,
} from './components';

import {
  MatToolbarModule,
  MatIconRegistry
} from '@angular/material';


import {
  ApiService,
  AuthService,
  UserService,
  ConfigService
} from './services';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        FooterComponent,
      ],
      imports: [
        RouterTestingModule,
        MatToolbarModule
      ],
      providers: [
        MatIconRegistry,
        {
          provide: ApiService,
          useClass: MockApiService
        },
        AuthService,
        UserService,
        ConfigService
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

});
