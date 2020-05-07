import { Injectable, OnInit } from '@angular/core';
import { WebsocketService, ApiService } from '.';
import { SocketOption } from '../model/SocketOption.model';

@Injectable()
export class LogsWebsocketService extends WebsocketService {

    constructor(private apiService: ApiService){
        super(new SocketOption('/quartz-manager/logs', '/topic/logs', apiService.getToken))
    }

}