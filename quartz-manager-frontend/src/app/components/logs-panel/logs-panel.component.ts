import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

import {LogsWebsocketService, ApiService} from '../../services';
import {Observable} from 'rxjs';

@Component({
  selector: 'logs-panel',
  templateUrl: './logs-panel.component.html',
  styleUrls: ['./logs-panel.component.scss']
})
export class LogsPanelComponent implements OnInit {

  MAX_LOGS = 30;

  logs = new Array();

  constructor(
    private logsWebsocketService: LogsWebsocketService,
    private apiService: ApiService
  ) {
  }

  ngOnInit() {
    const obs = this.logsWebsocketService.getObservable()
    obs.subscribe({
      'next': this.onNewLogMsg,
      'error': (err) => {
        console.log(err)
      }
    });
  }

  onNewLogMsg = (receivedMsg) => {
    if (receivedMsg.type === 'SUCCESS') {
      this._showNewLog(receivedMsg.message);
    } else if (receivedMsg.type === 'ERROR') {
      this._refreshSession();
    } // if websocket has been closed for session expiration, try to refresh it
  };

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
