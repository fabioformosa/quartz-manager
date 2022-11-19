import {Component, OnInit, ViewChild} from '@angular/core';
import {
  ConfigService,
  UserService
} from '../../services';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import {TriggerKey} from '../../model/triggerKey.model';
import {SimpleTriggerConfigComponent} from '../../components/simple-trigger-config';
import {TriggerListComponent} from '../../components';

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

  constructor(
  ) { }

  ngOnInit() {
  }

  onNewTriggerRequested() {
    this.triggerConfigComponent.openTriggerForm();
  }

  onNewTriggerCreated(newTrigger: SimpleTrigger) {
    this.triggerListComponent.onNewTrigger(newTrigger);
  }

  setSelectedTrigger(triggerKey: TriggerKey) {
    this.selectedTriggerKey = triggerKey;
  }

}
