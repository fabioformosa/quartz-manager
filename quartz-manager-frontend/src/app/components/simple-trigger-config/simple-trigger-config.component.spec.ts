import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {MatCardModule} from '@angular/material/card';
import {SimpleTriggerConfigComponent} from './simple-trigger-config.component';
import {ApiService, ConfigService, CONTEXT_PATH, SchedulerService} from '../../services';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {By} from '@angular/platform-browser';
import {RouterTestingModule} from '@angular/router/testing';
import {MatIconModule} from '@angular/material/icon';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatNativeDateModule} from '@angular/material/core';
import {MatInputModule} from '@angular/material/input';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {TriggerKey} from '../../model/triggerKey.model';
import {Trigger} from '../../model/trigger.model';
import {JobDetail} from '../../model/jobDetail.model';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import JobService from '../../services/job.service';
import {MatSelectModule} from '@angular/material/select';
import {MisfireInstruction} from '../../model/misfire-instruction.model';

describe('SimpleTriggerConfig', () => {

  let component: SimpleTriggerConfigComponent;
  let fixture: ComponentFixture<SimpleTriggerConfigComponent>;

  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync( () => {
    TestBed.configureTestingModule({
      imports: [FormsModule,  MatFormFieldModule, MatFormFieldModule, MatSelectModule, MatInputModule, NoopAnimationsModule,
        MatNativeDateModule, ReactiveFormsModule,
        MatCardModule, MatIconModule, HttpClientTestingModule, RouterTestingModule],
      declarations: [SimpleTriggerConfigComponent],
      providers: [SchedulerService, ApiService, ConfigService, JobService, FormBuilder],
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
    httpTestingController.expectNone(`${CONTEXT_PATH}/simple-triggers/my-simple-trigger`);
  });

  function setInputValue(componentDe: DebugElement, inputSelector: string, value: string) {
    const inputDe = componentDe.query(By.css(inputSelector));
    const inputEl = inputDe.nativeElement;
    inputEl.value = value;
    inputEl.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }

  function setDropdownValue(componentDe: DebugElement, dropdownSelector: string, value: string) {
    const dropdownDe = componentDe.query(By.css(dropdownSelector));
    const dropdownEl = dropdownDe.nativeElement;
    dropdownEl.value = value;
    dropdownEl.dispatchEvent(new Event('change'));
    fixture.detectChanges();
  }
  function setDropdownValueByIndex(componentDe: DebugElement, dropdownSelector: string, index: number) {
    const dropdownDe = componentDe.query(By.css(dropdownSelector));
    const dropdownEl = dropdownDe.nativeElement;
    dropdownEl.value = dropdownEl.options[index].value;
    dropdownEl.dispatchEvent(new Event('change'));
    fixture.detectChanges();
  }

  async function setMatSelectValueByIndex(componentDe: DebugElement, dropdownSelector: string, index: number) {
    const dropdownDe = componentDe.query(By.css(dropdownSelector));
    dropdownDe.nativeElement.click();
    fixture.detectChanges();
    const matOptionDe = componentDe.query(By.css('.mat-mdc-select-panel')).queryAll(By.css('.mat-mdc-option'));
    matOptionDe[index].nativeElement.click();
    fixture.detectChanges();
  }

  function openFormAndFillAllMandatoryFields() {
    component.openTriggerForm();
    fixture.detectChanges();

    const getJobsReq = httpTestingController.expectOne(`${CONTEXT_PATH}/jobs`);
    getJobsReq.flush(['TestJob']);

    const componentDe: DebugElement = fixture.debugElement;

    const submitButton = componentDe.query(By.css('form button[color="primary"]'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Submit');
    expect(submitButton.nativeElement.getAttribute('disabled')).toEqual('');

    setInputValue(componentDe, '#triggerName', 'test-trigger');
    expect(component.simpleTriggerReactiveForm.controls.triggerName.value).toEqual('test-trigger');
    expect(submitButton.nativeElement.getAttribute('disabled')).toEqual('');
    setMatSelectValueByIndex(componentDe, '#misfireInstruction', 0);
    expect(component.simpleTriggerReactiveForm.controls.misfireInstruction.value).toEqual('MISFIRE_INSTRUCTION_FIRE_NOW');
    expect(submitButton.nativeElement.getAttribute('disabled')).toEqual('');
    setMatSelectValueByIndex(componentDe, '#jobClass', 0);
    expect(submitButton.nativeElement.getAttribute('disabled')).toEqual(null);

    setInputValue(componentDe, '#repeatCount', '1000');
    expect(submitButton.nativeElement.getAttribute('disabled')).toEqual('');

    setInputValue(componentDe, '#repeatInterval', '2000');
    expect(submitButton.nativeElement.getAttribute('disabled')).toEqual(null);
  }

  it('should enabled the submit only when the form is valid', () => {
    openFormAndFillAllMandatoryFields();
  });

  it('should emit an event when a new trigger is submitted', () => {
    const componentDe: DebugElement = fixture.debugElement;
    const mockTrigger = new Trigger();
    mockTrigger.triggerKeyDTO = new TriggerKey('test-trigger', null);
    mockTrigger.jobDetailDTO = <JobDetail>{jobClassName: 'TestJob', description: null};
    mockTrigger.misfireInstruction = MisfireInstruction.MISFIRE_INSTRUCTION_FIRE_NOW;

    openFormAndFillAllMandatoryFields();

    setInputValue(componentDe, '#repeatInterval', '2000');
    expect(component.simpleTriggerReactiveForm.controls.triggerRecurrence.value.repeatInterval).toEqual(2000);
    setInputValue(componentDe, '#repeatCount', '100');
    expect(component.simpleTriggerReactiveForm.controls.triggerRecurrence.value.repeatCount).toEqual(100);

    const submitButton = componentDe.query(By.css('form button[color="primary"]'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Submit');

    let actualNewTrigger;
    component.onNewTrigger.subscribe(simpleTrigger => actualNewTrigger = simpleTrigger);

    submitButton.nativeElement.click();

    const postSimpleTriggerReq = httpTestingController.expectOne(`${CONTEXT_PATH}/simple-triggers/test-trigger`);
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
    mockTrigger.misfireInstruction = MisfireInstruction.MISFIRE_INSTRUCTION_FIRE_NOW;
    const getSimpleTriggerReq = httpTestingController.expectOne(`${CONTEXT_PATH}/simple-triggers/test-trigger`);
    getSimpleTriggerReq.flush(mockTrigger);

    component.simpleTriggerReactiveForm.setValue({
      triggerName: 'test-trigger',
      jobClass: 'TestJob',
      triggerRecurrence: {
        repeatInterval: 2000,
        repeatCount: 100,
      },
      triggerPeriod: {
        startDate: null,
        endDate: null
      },
      misfireInstruction: MisfireInstruction.MISFIRE_INSTRUCTION_FIRE_NOW.toString()
      });

    component.openTriggerForm();
    fixture.detectChanges();

    const componentDe: DebugElement = fixture.debugElement;
    setInputValue(componentDe, '#repeatInterval', '4000');
    expect(component.simpleTriggerReactiveForm.controls.triggerRecurrence.value.repeatInterval).toEqual(4000);

    const submitButton = componentDe.query(By.css('form button[color="primary"]'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Submit');

    let actualNewTrigger;
    component.onNewTrigger.subscribe(simpleTrigger => actualNewTrigger = simpleTrigger);

    submitButton.nativeElement.click();

    const putSimpleTriggerReq = httpTestingController.expectOne(`${CONTEXT_PATH}/simple-triggers/test-trigger`);
    putSimpleTriggerReq.flush(mockTrigger);

    expect(actualNewTrigger).toBeUndefined();
  });

  it('should fetch and display the trigger when the triggerKey is passed as input', () => {
    const mockTriggerKey = new TriggerKey('my-simple-trigger', null);
    component.triggerKey = mockTriggerKey;

    component.trigger = new SimpleTrigger();
    component.trigger.triggerKeyDTO = mockTriggerKey;

    fixture.detectChanges();

    const mockTrigger = new Trigger();
    mockTrigger.triggerKeyDTO = mockTriggerKey;
    mockTrigger.jobDetailDTO = <JobDetail>{jobClassName: 'TestJob', description: null};
    const getSimpleTriggerReq = httpTestingController.expectOne(`${CONTEXT_PATH}/simple-triggers/my-simple-trigger`);
    getSimpleTriggerReq.flush(mockTrigger);

    const componentDe: DebugElement = fixture.debugElement;
    const submitButton = componentDe.query(By.css('form button'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Reschedule');
  });

  it('should display the form if the openTriggerForm method is called', () => {
    component.openTriggerForm();
    fixture.detectChanges();

    const componentDe: DebugElement = fixture.debugElement;
    const submitButton = componentDe.query(By.css('form button[color="primary"]'));
    expect(submitButton.nativeElement.textContent.trim()).toEqual('Submit');
  });

  it('should display the warning if there are no eligible jobs', () => {
    fixture.detectChanges();
    const getJobsReq = httpTestingController.expectOne(`${CONTEXT_PATH}/jobs`);
    getJobsReq.flush([]);
    fixture.detectChanges();

    component.openTriggerForm();
    fixture.detectChanges();

    const componentDe: DebugElement = fixture.debugElement;
    const warningCard = componentDe.query(By.css('#noEligibleJobsAlert'));
    expect(warningCard).toBeTruthy();
  });

  it('should not display the warning if there are eligible jobs', () => {
    fixture.detectChanges();
    const getJobsReq = httpTestingController.expectOne(`${CONTEXT_PATH}/jobs`);
    getJobsReq.flush(['sampleJob']);
    fixture.detectChanges();

    component.openTriggerForm();
    fixture.detectChanges();

    const componentDe: DebugElement = fixture.debugElement;
    const warningCard = componentDe.query(By.css('#noEligibleJobsAlert'));
    expect(warningCard).toBeFalsy();
  });



});
