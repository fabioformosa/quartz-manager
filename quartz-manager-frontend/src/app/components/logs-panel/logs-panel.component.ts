import {Component, Input, NgZone, OnDestroy, OnInit} from '@angular/core';

import {ApiService} from '../../services';
import {LogsRxWebsocketService} from '../../services/logs.rx-websocket.service';
import {map} from 'rxjs/operators';
import {TriggerKey} from '../../model/triggerKey.model';


@Component({
    selector: 'logs-panel',
    templateUrl: './logs-panel.component.html',
    styleUrls: ['./logs-panel.component.scss'],
    standalone: false
})
export class LogsPanelComponent implements OnInit, OnDestroy {

  MAX_LOGS = 30;

  logs = new Array();

  selectedTriggerName: string;

  topicSubscription;

  private selectedTriggerKey: TriggerKey;

  constructor(
    private logsRxWebsocketService: LogsRxWebsocketService,
    private apiService: ApiService,
    private ngZone: NgZone
  ) {
  }

  @Input()
  set triggerKey(triggerKey: TriggerKey) {
    if (!triggerKey || !triggerKey.name) {
      this._unsubscribeFromTopic();
      this.selectedTriggerKey = null;
      this.selectedTriggerName = null;
      this._resetLogs();
      return;
    }

    if (this.selectedTriggerKey?.name === triggerKey.name) {
      return;
    }

    this._resetLogs();
    this.selectedTriggerKey = {...triggerKey} as TriggerKey;
    this.selectedTriggerName = triggerKey.name;
    this._subscribeToTheTopic(this.selectedTriggerKey);
  }

  isWaitingForLogs = (): boolean => !!this.selectedTriggerName && (!this.logs || this.logs.length === 0);

  ngOnInit() {
  }

  private _subscribeToTheTopic = (triggerKey: TriggerKey) => {
    this._unsubscribeFromTopic();
    this.topicSubscription = this.logsRxWebsocketService.watch(`/topic/logs/${triggerKey.name}`)
      .pipe(map((msg: any) => JSON.parse(msg.body)))
      .subscribe(logRecord => this.ngZone.run(() => this._showNewLog(logRecord)), (err) => {
        console.log(err);
        // TODO in case of 401
        // this.apiService.get('/quartz-manager/session/refresh');
      });
  };

  ngOnDestroy() {
    this._unsubscribeFromTopic();
  }

  private _unsubscribeFromTopic() {
    if (this.topicSubscription) {
      this.topicSubscription.unsubscribe();
      this.topicSubscription = null;
    }
  }

  private _resetLogs() {
    this.logs = [];
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
