import {TestBed, async, waitForAsync} from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { MockApiService } from './services/mocks/api.service.mock';

import { FooterComponent} from './components';

import {MatIconRegistry} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';


import {
  ApiService,
  AuthService,
  UserService,
  ConfigService
} from './services';

describe('AppComponent', () => {
  beforeEach(waitForAsync(() => {
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
