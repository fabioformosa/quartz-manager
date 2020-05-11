import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatCardModule} from '@angular/material/card';
import { MatInputModule} from '@angular/material/input';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';

import { SignupComponent } from './signup.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import {
    MockUserService,
    MockApiService
  } from '../../services/mocks';
import {
      UserService,
      AuthService,
      ApiService,
      ConfigService
    } from '../../services';

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
