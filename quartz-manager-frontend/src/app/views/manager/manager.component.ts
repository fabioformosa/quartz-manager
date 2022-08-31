import {Component, OnInit, ViewChild} from '@angular/core';
import {
  ConfigService,
  UserService
} from '../../services';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import {TriggerKey} from '../../model/triggerKey.model';
import {SimpleTriggerConfigComponent} from '../../components/simple-trigger-config';

@Component({
  selector: 'manager',
  templateUrl: './manager.component.html',
  styleUrls: ['./manager.component.scss']
})
export class ManagerComponent implements OnInit {

  @ViewChild(SimpleTriggerConfigComponent)
  private triggerConfigComponent!: SimpleTriggerConfigComponent;

  newTriggerFormOpened = false;

  newTriggers = new Array<SimpleTrigger>();
  selectedTriggerKey: TriggerKey;

  constructor(
    private config: ConfigService,
    private userService: UserService
  ) { }

  ngOnInit() {
  }

  onNewTriggerRequested() {
    this.triggerConfigComponent.openTriggerForm();
  }

  onNewTrigger(newTrigger: SimpleTrigger) {
    this.newTriggers.push(newTrigger);
  }

  setSelectedTrigger(triggerKey: TriggerKey) {
    this.selectedTriggerKey = triggerKey;
  }

  onTriggerFormToggled(formOpened: boolean) {
    this.newTriggerFormOpened = formOpened;
  }
}
