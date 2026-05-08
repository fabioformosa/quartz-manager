import { Component, OnInit, ViewChild } from '@angular/core';
import {  SimpleTrigger } from '../../model/simple-trigger.model';
import { TriggerKey } from '../../model/triggerKey.model';
import { SimpleTriggerConfigComponent } from '../../components/simple-trigger-config';
import { TriggerListComponent } from '../../components';

@Component({
  selector: 'manager',
  templateUrl: './manager.component.html',
  styleUrls: ['./manager.component.scss']
})
export class ManagerComponent implements OnInit {
  @ViewChild(SimpleTriggerConfigComponent)
  private triggerConfigComponent!: SimpleTriggerConfigComponent;

  @ViewChild(TriggerListComponent)
  private triggerListComponent: TriggerListComponent;

  newTriggerFormOpened = false;

  selectedTriggerKey: TriggerKey;

  monitoredTriggerKey: TriggerKey;

  constructor() {}

  ngOnInit() {}

  onNewTriggerRequested() {
    this.selectedTriggerKey = null;
    this.monitoredTriggerKey = null;
    this.newTriggerFormOpened = true;
    if (this.triggerConfigComponent) {
      this.triggerConfigComponent.openNewTriggerForm();
    }
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
