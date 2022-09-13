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
import {MatInputModule} from '@angular/material/input';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TriggerKey} from '../../model/triggerKey.model';
import {Trigger} from '../../model/trigger.model';
import {JobDetail} from '../../model/jobDetail.model';
import {SimpleTriggerForm} from '../../model/simple-trigger.form';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import JobService from '../../services/job.service';

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
      providers: [SchedulerService, ApiService, ConfigService, JobService],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SimpleTriggerConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should fetch no triggers at the init', () => {
    expect(component).toBeTruthy();
    httpTestingController.expectNone('/quartz-manager/simple-triggers/my-simple-trigger');
  });

  function setInputValue(componentDe: DebugElement, inputSelector: string, value: string) {
    const inputDe = componentDe.query(By.css(inputSelector));
    const inputEl = inputDe.nativeElement;
    inputEl.value = value;
    inputEl.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }

  it('should emit an event when a new trigger is submitted', () => {
    const mockTrigger = new Trigger();
    mockTrigger.triggerKeyDTO = new TriggerKey('test-trigger', null);
    mockTrigger.jobDetailDTO = <JobDetail>{jobClassName: 'TestJob', description: null};

    component.openTriggerForm();
    fixture.detectChanges();

    const componentDe: DebugElement = fixture.debugElement;
    setInputValue(componentDe, '#triggerName', 'test-trigger');
    expect(component.simpleTriggerForm.triggerName).toEqual('test-trigger');
    setInputValue(componentDe, '#jobClass', 'TestJob');
    // setInputValue(componentDe, '#startDate', '19/11/2022, 10:34:00 PM');
    // setInputValue(componentDe, '#endDate', '21/11/2022, 10:34:00 PM');
    setInputValue(componentDe, '#repeatInterval', '2000');
    expect(component.simpleTriggerForm.repeatInterval).toEqual(2000);
    setInputValue(componentDe, '#repeatCount', '100');
    expect(component.simpleTriggerForm.repeatCount).toEqual(100);

    const submitButton = componentDe.query(By.css('form > button[color="primary"]'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Submit');

    let actualNewTrigger;
    component.onNewTrigger.subscribe(simpleTrigger => actualNewTrigger = simpleTrigger);

    submitButton.nativeElement.click();

    const postSimpleTriggerReq = httpTestingController.expectOne('/quartz-manager/simple-triggers/test-trigger');
    postSimpleTriggerReq.flush(mockTrigger);

    expect(actualNewTrigger).toEqual(mockTrigger);
  });

  it('should not emit an event when an existing trigger is edited', () => {
    const mockTriggerKey = new TriggerKey('test-trigger', null);
    component.triggerKey = mockTriggerKey;
    fixture.detectChanges();

    const mockTrigger = new SimpleTrigger();
    mockTrigger.triggerKeyDTO = new TriggerKey('test-trigger', null);
    mockTrigger.jobDetailDTO = <JobDetail>{jobClassName: 'TestJob', description: null};
    mockTrigger.mayFireAgain = true;
    const getSimpleTriggerReq = httpTestingController.expectOne('/quartz-manager/simple-triggers/test-trigger');
    getSimpleTriggerReq.flush(mockTrigger);

    component.simpleTriggerForm = <SimpleTriggerForm>{
      triggerName: 'test-trigger',
      jobClass: 'TestJob',
      repeatInterval: 2000,
      repeatCount: 100
    };

    component.openTriggerForm();
    fixture.detectChanges();

    const componentDe: DebugElement = fixture.debugElement;
    setInputValue(componentDe, '#repeatInterval', '4000');
    expect(component.simpleTriggerForm.repeatInterval).toEqual(4000);

    const submitButton = componentDe.query(By.css('form > button[color="primary"]'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Submit');

    let actualNewTrigger;
    component.onNewTrigger.subscribe(simpleTrigger => actualNewTrigger = simpleTrigger);

    submitButton.nativeElement.click();

    const putSimpleTriggerReq = httpTestingController.expectOne('/quartz-manager/simple-triggers/test-trigger');
    putSimpleTriggerReq.flush(mockTrigger);

    expect(actualNewTrigger).toBeUndefined();
  });

  it('should fetch and display the trigger when the triggerKey is passed as input', () => {
    const mockTriggerKey = new TriggerKey('my-simple-trigger', null);
    component.triggerKey = mockTriggerKey;
    fixture.detectChanges();

    const mockTrigger = new Trigger();
    mockTrigger.triggerKeyDTO = mockTriggerKey;
    mockTrigger.jobDetailDTO = <JobDetail>{jobClassName: 'TestJob', description: null};
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
