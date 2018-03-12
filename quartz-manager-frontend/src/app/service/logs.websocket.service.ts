import { Injectable, OnInit } from '@angular/core';
import { WebsocketService } from '.';
import { SocketOption } from '../model/SocketOption.model';

Injectable()
export class LogsWebsocketService extends WebsocketService {

    constructor(){
        super(new SocketOption('/quartz-manager/logs', '/topic/logs'))
    }

}