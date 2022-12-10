import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {SchedulerControlComponent} from './scheduler-control.component';
import {ApiService, ConfigService, SchedulerService, UserService} from '../../services';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {Scheduler} from '../../model/scheduler.model';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';

describe('SchedulerControlComponent', () => {

  let component: SchedulerControlComponent;
  let fixture: ComponentFixture<SchedulerControlComponent>;

  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [MatCardModule, MatDividerModule, MatIconModule, HttpClientTestingModule, RouterTestingModule],
      declarations: [SchedulerControlComponent],
      providers: [UserService, SchedulerService, ApiService, ConfigService]
    }).compileComponents();

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchedulerControlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display the play button at the beginning since the scheduler is stopped', () => {
    expect(component).toBeDefined();
    const getSchedulerReq = httpTestingController.expectOne('/quartz-manager/scheduler');
    const mockScheduler = new Scheduler('test-scheduler', 'test-id', 'STOPPED', []);
    getSchedulerReq.flush(mockScheduler);

    expect(component.scheduler).toEqual(mockScheduler);
    expect(component.scheduler.status).toEqual('STOPPED');
    fixture.detectChanges();

    const schedulerControlComponentDe: DebugElement = fixture.debugElement;
    const schedulerBtnDe = schedulerControlComponentDe.query(By.css('#schedulerControllerBtn'));
    expect(schedulerBtnDe).toBeTruthy();

    const playIconDe = schedulerBtnDe.query(By.css('.fa-play'));
    expect(playIconDe).toBeTruthy();
  });

  it('should switch the button to pause when the scheduler is started', () => {
    expect(component).toBeDefined();
    const getSchedulerReq = httpTestingController.expectOne('/quartz-manager/scheduler');
    const mockScheduler = new Scheduler('test-scheduler', 'test-id', 'STOPPED', []);
    getSchedulerReq.flush(mockScheduler);
    fixture.detectChanges();

    const schedulerControlComponentDe: DebugElement = fixture.debugElement;
    let schedulerBtnDe = schedulerControlComponentDe.query(By.css('#schedulerControllerBtn'));
    expect(schedulerBtnDe).toBeTruthy();
    const playIconDe = schedulerBtnDe.query(By.css('.fa-play'));
    expect(playIconDe).toBeTruthy();

    schedulerBtnDe.nativeElement.click();
    const startSchedulerReq = httpTestingController.expectOne('/quartz-manager/scheduler/run');
    startSchedulerReq.flush(null);
    fixture.detectChanges();

    schedulerBtnDe = schedulerControlComponentDe.query(By.css('#schedulerControllerBtn'));
    const pauseIconDe = schedulerBtnDe.query(By.css('.fa-pause'));
    expect(pauseIconDe).toBeTruthy();

  })

  it('should switch the button to play when the scheduler is stopped', () => {
    expect(component).toBeDefined();
    const getSchedulerReq = httpTestingController.expectOne('/quartz-manager/scheduler');
    const mockScheduler = new Scheduler('test-scheduler', 'test-id', 'RUNNING', []);
    getSchedulerReq.flush(mockScheduler);
    fixture.detectChanges();

    const schedulerControlComponentDe: DebugElement = fixture.debugElement;
    let schedulerBtnDe = schedulerControlComponentDe.query(By.css('#schedulerControllerBtn'));
    expect(schedulerBtnDe).toBeTruthy();
    const pauseIconDe = schedulerBtnDe.query(By.css('.fa-pause'));
    expect(pauseIconDe).toBeTruthy();

    schedulerBtnDe.nativeElement.click();
    const startSchedulerReq = httpTestingController.expectOne('/quartz-manager/scheduler/pause');
    startSchedulerReq.flush(null);
    fixture.detectChanges();

    schedulerBtnDe = schedulerControlComponentDe.query(By.css('#schedulerControllerBtn'));
    const playIconDe = schedulerBtnDe.query(By.css('.fa-play'));
    expect(playIconDe).toBeTruthy();

  })

});
