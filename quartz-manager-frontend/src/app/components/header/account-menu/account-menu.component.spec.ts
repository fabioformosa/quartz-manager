import { ComponentFixture, TestBed, inject, waitForAsync} from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';

import {
  AuthService,
  ConfigService,
  ApiService,
  UserService
} from '../../../services';
import {
  MockUserService,
  MockApiService
} from '../../../services/mocks';
import { AccountMenuComponent } from './account-menu.component';

describe('AccountMenuComponent', () => {
  let component: AccountMenuComponent;
  let fixture: ComponentFixture<AccountMenuComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      providers: [
        {
          provide: UserService,
          useClass: MockUserService
        },
        {
          provide: ApiService,
          useClass: MockApiService
        },
        AuthService,
        ConfigService
      ],
      declarations: [AccountMenuComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
