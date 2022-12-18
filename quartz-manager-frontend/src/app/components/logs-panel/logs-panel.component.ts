import {Component, OnInit, Input, Output, EventEmitter, OnDestroy} from '@angular/core';

import {LogsWebsocketService, ApiService, getBaseUrl, CONTEXT_PATH, QuartzManagerWebsocketMessage} from '../../services';
import {Observable} from 'rxjs';
import {RxStompService, } from '../../services/rx-stomp.service';
import {RxStompConfig} from '@stomp/rx-stomp/esm6/rx-stomp-config';
import {LogsRxWebsocketService} from '../../services/logs.rx-websocket.service';
import {map} from 'rxjs/operators';


@Component({
  selector: 'logs-panel',
  templateUrl: './logs-panel.component.html',
  styleUrls: ['./logs-panel.component.scss']
})
export class LogsPanelComponent implements OnInit, OnDestroy {

  MAX_LOGS = 30;

  logs = new Array();

  topicSubscription;

  constructor(
    // private logsWebsocketService: LogsWebsocketService,
    private logsRxWebsocketService: LogsRxWebsocketService,
    private apiService: ApiService
  ) {
  }

  ngOnInit() {
    // const obs = this.logsWebsocketService.getObservable()
    // obs.subscribe({
    //   'next': this.onNewLogMsg,
    //   'error': (err) => {
    //     console.log(err)
    //   }
    // });

    this.topicSubscription = this.logsRxWebsocketService.watch('/topic/logs')
      .pipe(map(msg => JSON.parse(msg.body)))
      .subscribe(this._showNewLog, (err) => {
        console.log(err);
        // TODO in case of 401
        // this.apiService.get('/quartz-manager/session/refresh');
      });
  }

  ngOnDestroy() {
    this.topicSubscription.unsubscribe();
  }

  // onNewLogMsg = (receivedMsg) => {
  //   if (receivedMsg.body.type === 'SUCCESS') {
  //     this._showNewLog(receivedMsg.body.message);
  //   } else if (receivedMsg.body.type === 'ERROR') {
  //     this._refreshSession();
  //   } // if websocket has been closed for session expiration, try to refresh it
  // };

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
