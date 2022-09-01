import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MatCardModule} from '@angular/material/card';
import {SimpleTriggerConfigComponent} from './simple-trigger-config.component';
import {ApiService, ConfigService, SchedulerService} from '../../services';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {By} from '@angular/platform-browser';
import {RouterTestingModule} from '@angular/router/testing';
import {MatIconModule} from '@angular/material/icon';
import {FormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatNativeDateModule} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatInputModule} from '@angular/material/input';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TriggerKey} from '../../model/triggerKey.model';
import {Trigger} from '../../model/trigger.model';
import {NgxMatDatetimePickerModule} from '@angular-material-components/datetime-picker';
import { NgxMatMomentModule } from '@angular-material-components/moment-adapter';

describe('SimpleTriggerConfig', () => {

  let component: SimpleTriggerConfigComponent;
  let fixture: ComponentFixture<SimpleTriggerConfigComponent>;

  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(async( () => {
    TestBed.configureTestingModule({
      imports: [FormsModule, MatFormFieldModule, MatFormFieldModule, MatInputModule, BrowserAnimationsModule,
        MatNativeDateModule,
        MatCardModule, MatIconModule, HttpClientTestingModule, RouterTestingModule],
      declarations: [SimpleTriggerConfigComponent],
      providers: [SchedulerService, ApiService, ConfigService],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SimpleTriggerConfigComponent);
    component = fixture.componentInstance;
  });

  it('should fetch no triggers at the init', () => {
    expect(component).toBeTruthy();
    httpTestingController.expectNone('/quartz-manager/simple-triggers/my-simple-trigger');
  });

  it('should fetch and display the trigger when the triggerKey is passed as input', () => {
    const mockTriggerKey = new TriggerKey('my-simple-trigger', null);
    component.triggerKey = mockTriggerKey;
    fixture.detectChanges();

    const mockTrigger = new Trigger();
    mockTrigger.triggerKeyDTO = mockTriggerKey;
    const getSimpleTriggerReq = httpTestingController.expectOne('/quartz-manager/simple-triggers/my-simple-trigger');
    getSimpleTriggerReq.flush(mockTrigger);

    const componentDe: DebugElement = fixture.debugElement;
    const submitButton = componentDe.query(By.css('form > button'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Reschedule');
  });

  it('should display the form if the openTriggerForm method is called', () => {
    component.openTriggerForm();
    fixture.detectChanges();

    const componentDe: DebugElement = fixture.debugElement;
    const submitButton = componentDe.query(By.css('form > button[color="primary"]'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Submit');
  });



});
