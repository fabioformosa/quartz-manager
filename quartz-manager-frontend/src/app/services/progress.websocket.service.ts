import { Injectable, OnInit } from '@angular/core';
import { WebsocketService } from '.';
import { SocketOption } from '../model/SocketOption.model';

Injectable()
@Injectable()
export class ProgressWebsocketService extends WebsocketService {

    constructor(){
        super(new SocketOption('/quartz-manager/progress', '/topic/progress'))
    }

}