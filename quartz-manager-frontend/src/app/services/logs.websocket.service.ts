import { Injectable } from '@angular/core';
import { WebsocketService, ApiService, getBaseUrl } from '.';
import { SocketOption } from '../model/SocketOption.model';

@Injectable()
export class LogsWebsocketService extends WebsocketService {

    constructor(private apiService: ApiService){
        super(new SocketOption( getBaseUrl() +'/quartz-manager/logs', '/topic/logs', apiService.getToken))
    }

}