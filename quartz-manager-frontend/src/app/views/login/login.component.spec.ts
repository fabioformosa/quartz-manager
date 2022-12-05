import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { RouterTestingModule } from '@angular/router/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MockApiService } from '../../services/mocks/api.service.mock';
import { ReactiveFormsModule } from '@angular/forms';

import {
  ApiService,
  AuthService,
  UserService,
  ConfigService
} from '../../services';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule
      ],
      providers: [
        UserService,
        {
          provide: ApiService,
          useClass: MockApiService
        },
        ConfigService,
        AuthService
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
