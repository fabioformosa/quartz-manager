import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import {  SimpleTrigger } from '../../model/simple-trigger.model';
import { TriggerKey } from '../../model/triggerKey.model';
import { SimpleTriggerConfigComponent } from '../../components/simple-trigger-config';
import { TriggerListComponent } from '../../components';

@Component({
    selector: 'manager',
    templateUrl: './manager.component.html',
    styleUrls: ['./manager.component.scss'],
    standalone: false
})
export class ManagerComponent implements OnInit, AfterViewInit {
  @ViewChild(SimpleTriggerConfigComponent)
  private triggerConfigComponent!: SimpleTriggerConfigComponent;

  @ViewChild(TriggerListComponent)
  private triggerListComponent: TriggerListComponent;

  newTriggerFormOpened = false;

  selectedTriggerKey: TriggerKey;

  monitoredTriggerKey: TriggerKey;

  private pendingNewTriggerRequest = false;

  constructor() {}

  ngOnInit() {}

  ngAfterViewInit() {
    if (this.pendingNewTriggerRequest) {
      queueMicrotask(() => this.openNewTriggerForm());
    }
  }

  onNewTriggerRequested() {
    this.selectedTriggerKey = null;
    this.monitoredTriggerKey = null;
    if (this.triggerConfigComponent) {
      this.openNewTriggerForm();
    } else {
      this.pendingNewTriggerRequest = true;
    }
  }

  private openNewTriggerForm() {
    this.newTriggerFormOpened = true;
    this.pendingNewTriggerRequest = false;
    this.triggerConfigComponent.openNewTriggerForm();
  }

  onNewTriggerCreated(newTrigger: SimpleTrigger) {
    this.triggerListComponent.onNewTrigger(newTrigger);
    this.newTriggerFormOpened = false;
  }

  setSelectedTrigger(triggerKey: TriggerKey) {
    this.selectedTriggerKey = triggerKey;
    this.monitoredTriggerKey = triggerKey;
    this.newTriggerFormOpened = false;
  }

  monitorTrigger(triggerKey: TriggerKey) {
    this.monitoredTriggerKey = triggerKey;
  }

  setNewTriggerFormOpened(opened: boolean) {
    this.newTriggerFormOpened = opened;
  }
}
