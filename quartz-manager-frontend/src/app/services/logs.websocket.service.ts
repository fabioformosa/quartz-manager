import {Injectable} from '@angular/core';
import {WebsocketService, ApiService, getBaseUrl, CONTEXT_PATH} from '.';
import {SocketOption} from '../model/SocketOption.model';

@Injectable()
export class LogsWebsocketService extends WebsocketService {

  constructor(private apiService: ApiService) {
    super(new SocketOption(getBaseUrl() + `${CONTEXT_PATH}/logs`, '/topic/logs', apiService.getToken))
  }

}
