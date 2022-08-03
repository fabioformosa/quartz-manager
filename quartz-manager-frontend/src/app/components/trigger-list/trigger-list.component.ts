import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {TriggerService} from '../../services/trigger.service';
import {TriggerKey} from '../../model/triggerKey.model';

@Component({
  selector: 'qrzmng-trigger-list',
  templateUrl: './trigger-list.component.html',
  styleUrls: ['./trigger-list.component.scss']
})
export class TriggerListComponent implements OnInit {

  loading = true;
  triggerKeys: Array<TriggerKey> = [];

  @Output() openedNewTriggerFormEvent = new EventEmitter<boolean>();

  constructor(
    private triggerService: TriggerService
  ) { }

  ngOnInit() {
    this.loading = true;
    this.fetchTriggers();
  }

  private fetchTriggers() {
    this.triggerService.fetchTriggers()
      .subscribe((triggerKeys: Array<TriggerKey>) => {
        this.triggerKeys = triggerKeys;
      })
  }

  openNewTriggerForm() {
    this.openedNewTriggerFormEvent.emit(true);
  }
}
