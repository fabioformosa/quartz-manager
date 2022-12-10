import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {ApiService, ConfigService, CONTEXT_PATH, TriggerService} from '../../services';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {TriggerListComponent} from './trigger-list.component';
import {MatListModule} from '@angular/material/list';
import {TriggerKey} from '../../model/triggerKey.model';
import {MatDialogModule} from '@angular/material/dialog';

describe('TriggerListComponent', () => {

  let component: TriggerListComponent;
  let fixture: ComponentFixture<TriggerListComponent>;

  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [MatCardModule, MatDialogModule, MatDividerModule,
        MatIconModule, MatListModule, HttpClientTestingModule, RouterTestingModule],
      declarations: [TriggerListComponent],
      providers: [TriggerService, ApiService, ConfigService]
    }).compileComponents();

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TriggerListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should select the first trigger of the list', () => {
    expect(component).toBeDefined();

    let actualSelectedTrigger: TriggerKey;
    component.onSelectedTrigger.subscribe(selectedTrigger => actualSelectedTrigger = selectedTrigger);

    const getTriggerListReq = httpTestingController.expectOne(`${CONTEXT_PATH}/triggers`);
    const mockExistingTriggers = new Array<TriggerKey>();
    const firstTriggerKey = new TriggerKey('trigger1', 'group1');
    mockExistingTriggers.push(firstTriggerKey);
    const secondTriggerKey = new TriggerKey('trigger2', 'group2');
    mockExistingTriggers.push(secondTriggerKey);
    getTriggerListReq.flush(mockExistingTriggers);
    fixture.detectChanges();

    const triggerListComponentDe: DebugElement = fixture.debugElement;
    const triggerItemList = triggerListComponentDe.queryAll(By.css('.triggerItemList'));
    expect(triggerItemList.length).toEqual(2);

    expect(actualSelectedTrigger).toEqual(firstTriggerKey);

  });

  it('should open the trigger form if the trigger list is empty', () => {
    expect(component).toBeDefined();

    let actualSelectedTrigger: TriggerKey;
    component.onSelectedTrigger.subscribe(selectedTrigger => actualSelectedTrigger = selectedTrigger);

    let expectedOpenedNewTriggerFormEvent: boolean;
    component.onNewTriggerClicked.subscribe(() => expectedOpenedNewTriggerFormEvent = true);

    const getTriggerListReq = httpTestingController.expectOne(`${CONTEXT_PATH}/triggers`);
    getTriggerListReq.flush(new Array<TriggerKey>());
    fixture.detectChanges();

    const triggerListComponentDe: DebugElement = fixture.debugElement;
    const triggerItemList = triggerListComponentDe.queryAll(By.css('.triggerItemList'));
    expect(triggerItemList.length).toEqual(0);

    expect(expectedOpenedNewTriggerFormEvent).toBeTruthy();
    expect(actualSelectedTrigger).toBeUndefined();
  });

});
