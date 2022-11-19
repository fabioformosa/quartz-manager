import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core'
import {ProgressWebsocketService, QuartzManagerWebsocketMessage} from '../../services';

import { Observable } from 'rxjs';
import TriggerFiredBundle from '../../model/trigger-fired-bundle.model';
// import {Message} from '@stomp/stompjs';

// import { Subscription } from 'rxjs/Subscription';
// import {StompService} from '@stomp/ng2-stompjs';

// import { QueueingSubject } from 'queueing-subject'
// import websocketConnect from 'rxjs-websockets'
// import 'rxjs/add/operator/share'
// import {ServerSocket} from '../../services/qz.socket.service'

@Component({
  selector: 'progress-panel',
  templateUrl: './progress-panel.component.html',
  styleUrls: ['./progress-panel.component.scss']
})
export class ProgressPanelComponent implements OnInit {

  progress: TriggerFiredBundle = new TriggerFiredBundle();
  percentageStr: string;

  // // Stream of messages
  // private subscription: Subscription;
  // public messages: Observable<Message>;
  // // Subscription status
  // public subscribed: boolean;
  // // Array of historic message (bodies)
  // public mq: Array<string> = [];


  constructor(
    private progressWebsocketService: ProgressWebsocketService,
    // private _stompService: StompService,
    // private serverSocket : ServerSocket
  ) { }

  onNewProgressMsg = (receivedMsg: QuartzManagerWebsocketMessage) => {
    if (receivedMsg.type === 'SUCCESS') {
      const newStatus = receivedMsg.message;
      this.progress = newStatus;
      this.percentageStr = this.progress.percentage + '%';
    }
  }

  ngOnInit() {
    const obs = this.progressWebsocketService.getObservable()
    obs.subscribe({
      'next' : this.onNewProgressMsg,
      'error' : (err) => {console.log(err)}
    });

    // this.subscribed = false;
    // this.subscribe();

    // this.serverSocket.connect()
    // this.socketSubscription = this.serverSocket.messages.subscribe((message: string) => {
    //   console.log('received message from server: ', message)
    // })
  }

  // public subscribe() {
  //   if (this.subscribed) {
  //     return;
  //   }

  //   // Stream of messages
  //   this.messages = this._stompService.subscribe('/topic/progress');

  //   // Subscribe a function to be run on_next message
  //   this.subscription = this.messages.subscribe(this.on_next);

  //   this.subscribed = true;
  // }

  // public on_next = (message: Message) => {
  //   this.mq.push(message.body + '\n');
  //   console.log(message);
  // }

}
