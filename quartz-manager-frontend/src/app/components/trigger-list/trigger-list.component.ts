import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TriggerService} from '../../services/trigger.service';
import {TriggerKey} from '../../model/triggerKey.model';
import {SimpleTrigger} from '../../model/simple-trigger.model';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'qrzmng-trigger-list',
  templateUrl: './trigger-list.component.html',
  styleUrls: ['./trigger-list.component.scss']
})
export class TriggerListComponent implements OnInit {

  @Input()
  newTriggers: Array<SimpleTrigger> = new Array<SimpleTrigger>();

  loading = true;

  triggerKeys: Array<TriggerKey> = new Array<TriggerKey>();

  @Output() onNewTriggerClicked = new EventEmitter<void>();
  triggerFormIsOpen = false;

  selectedTrigger: TriggerKey;
  @Output() onSelectedTrigger = new EventEmitter<TriggerKey>();

  constructor(
    private triggerService: TriggerService,
    public dialog: MatDialog
  ) { }

  ngOnInit() {
    this.loading = true;
    this.fetchTriggers();
  }

  @Input()
  set openedNewTriggerForm(triggerFormIsOpen: boolean){
    this.triggerFormIsOpen = triggerFormIsOpen;
  }

  getTriggerKeyList = () => {
    const newTriggerKeys = this.newTriggers.map(simpleTrigger => simpleTrigger.triggerKeyDTO);
    return newTriggerKeys.concat(this.triggerKeys);
  }

  private fetchTriggers() {
    this.triggerService.fetchTriggers()
      .subscribe((triggerKeys: Array<TriggerKey>) => {
        this.triggerKeys = triggerKeys;
        if (!triggerKeys || triggerKeys.length === 0) {
          this.onNewTriggerBtnClicked();
        } else {
          this.selectTrigger(this.triggerKeys[0]);
        }
      })
  }

  selectTrigger(triggerKey: TriggerKey) {
    this.selectedTrigger = triggerKey;
    this.onSelectedTrigger.emit(triggerKey);
  }

  onNewTriggerBtnClicked() {
    if (this.triggerKeys && this.triggerKeys.length > 0)
      this.dialog.open(UnsupportedMultipleJobsDialog)
    else
      this.onNewTriggerClicked.emit();
  }
}

@Component({
  template: 'Multiple jobs not supported yet - Coming Soon...',
})
export class UnsupportedMultipleJobsDialog {}
