import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
    MatCardModule, MatInputModule, MatProgressSpinnerModule, MatProgressBarModule
  } from '@angular/material';
import { SignupComponent } from './signup.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import {
    MockUserService,
    MockApiService
  } from 'app/services/mocks';
import {
      UserService,
      AuthService,
      ApiService,
      ConfigService
    } from 'app/services';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SignupComponent ],
      imports : [RouterTestingModule,
                 BrowserAnimationsModule,
                 MatCardModule,
                 MatInputModule,
                 MatProgressSpinnerModule,
                 MatProgressBarModule,
                 ReactiveFormsModule],
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
                 ConfigService]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
