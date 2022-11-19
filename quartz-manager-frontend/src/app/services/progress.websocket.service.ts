import {Injectable} from '@angular/core';
import {WebsocketService, ApiService, getBaseUrl, CONTEXT_PATH} from '.';
import {SocketOption} from '../model/SocketOption.model';

@Injectable()
export class ProgressWebsocketService extends WebsocketService {

  constructor(private apiService: ApiService) {
    super(new SocketOption(getBaseUrl() + `${CONTEXT_PATH}/progress`, '/topic/progress', apiService.getToken))
  }

}
