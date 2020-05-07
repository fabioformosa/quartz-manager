import { Injectable, OnInit } from '@angular/core';
import { WebsocketService, ApiService } from '.';
import { SocketOption } from '../model/SocketOption.model';

@Injectable()
export class ProgressWebsocketService extends WebsocketService {

    constructor(private apiService: ApiService){
        super(new SocketOption('/quartz-manager/progress', '/topic/progress', apiService.getToken))
    }

}