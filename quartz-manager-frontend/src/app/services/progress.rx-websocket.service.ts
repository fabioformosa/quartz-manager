import { Injectable } from '@angular/core';
import {RxStompService} from './rx-stomp.service';
import {ApiService} from './api.service';
import SockJS from 'sockjs-client';
import {CONTEXT_PATH, getBaseUrl} from './config.service';

@Injectable({
  providedIn: 'root'
})
export class ProgressRxWebsocketService extends RxStompService {

  constructor(private apiService: ApiService) {
    super({
      webSocketFactory: () => new SockJS(`${getBaseUrl()}${CONTEXT_PATH}/progress?access_token=${this.apiService.getToken()}`),
      heartbeatIncoming: 0,
      heartbeatOutgoing: 20000,
      reconnectDelay: 200,
      debug: (msg: string): void => {
        console.log(new Date(), msg);
      }
    });
  }
}
