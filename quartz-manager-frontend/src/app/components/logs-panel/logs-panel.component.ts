import {Component, Input, OnDestroy, OnInit} from '@angular/core';

import {ApiService} from '../../services';
import {LogsRxWebsocketService} from '../../services/logs.rx-websocket.service';
import {map} from 'rxjs/operators';
import {TriggerKey} from '../../model/triggerKey.model';


@Component({
  selector: 'logs-panel',
  templateUrl: './logs-panel.component.html',
  styleUrls: ['./logs-panel.component.scss']
})
export class LogsPanelComponent implements OnInit, OnDestroy {

  MAX_LOGS = 30;

  logs = new Array();

  topicSubscription;

  private selectedTriggerKey: TriggerKey;

  constructor(
    private logsRxWebsocketService: LogsRxWebsocketService,
    private apiService: ApiService
  ) {
  }

  @Input()
  set triggerKey(triggerKey: TriggerKey) {
    this.selectedTriggerKey = {...triggerKey} as TriggerKey;
    if (this.selectedTriggerKey && this.selectedTriggerKey.name) {
      this._subscribeToTheTopic(this.selectedTriggerKey);
    }
  }

  ngOnInit() {
  }

  private _subscribeToTheTopic = (triggerKey: TriggerKey) => {
    if (this.topicSubscription) {
      this.topicSubscription.unsubscribe();
    }
    this.topicSubscription = this.logsRxWebsocketService.watch(`/topic/logs/${triggerKey.name}`)
      .pipe(map(msg => JSON.parse(msg.body)))
      .subscribe(this._showNewLog, (err) => {
        console.log(err);
        // TODO in case of 401
        // this.apiService.get('/quartz-manager/session/refresh');
      });
  };

  ngOnDestroy() {
    if (this.topicSubscription) {
      this.topicSubscription.unsubscribe();
    }
    this.topicSubscription.unsubscribe();
    this.topicSubscription = null;
  }

  _showNewLog = (logRecord) => {
    if (this.logs.length > this.MAX_LOGS) {
      this.logs.pop();
    }

    this.logs.unshift({
      time: logRecord.date,
      type: logRecord.type,
      msg: logRecord.message,
      threadName: logRecord.threadName
    });
  }

  _refreshSession = () => {
    this.apiService.get('/quartz-manager/session/refresh')
  }

}
