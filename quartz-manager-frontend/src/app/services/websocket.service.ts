import {Observable, Subscriber} from 'rxjs';
import {SocketEndpoint} from '../model/SocketEndpoint.model'


import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import {SocketOption} from '../model/SocketOption.model';

interface WebsocketSubscriber {
  index: number,
  observer: Subscriber<any>
}

export interface QuartzManagerWebsocketMessage {
  type: string;
  message: any;
  headers: any;
  self: boolean;
}

export class WebsocketService {

  _options: SocketOption;

  _socket: SocketEndpoint = new SocketEndpoint();

  observableStompConnection: Observable<any>;
  subscribers: Array<WebsocketSubscriber> = [];
  subscriberIndex = 0;

  _messageIds: Array<any> = [];

  reconnectionPromise: any;

  constructor(options: SocketOption) {
    this._options = options
    this.createObservableSocket();
    this.connect();
  }

  getOptions = () => {
  }

  private createObservableSocket = () => {
    this.observableStompConnection = new Observable((observer) => {
      const subscriberIndex = this.subscriberIndex++;
      this.addToSubscribers({index: subscriberIndex, observer});
      return () => this.removeFromSubscribers(subscriberIndex);
    });
  }

  private addToSubscribers = (subscriber) => {
    this.subscribers.push(subscriber);
  }

  private removeFromSubscribers = (index) => {
    this.subscribers = this.subscribers.filter(subscriber => subscriber.index !== index);
  }

  getObservable = () => {
    return this.observableStompConnection;
  };

  getMessage = function (data): QuartzManagerWebsocketMessage  {
    const out: QuartzManagerWebsocketMessage = <QuartzManagerWebsocketMessage>{};
    out.type = 'SUCCESS';
    out.message = JSON.parse(data.body);
    out.headers = {};
    out.headers.messageId = data.headers['message-id'];

    const messageIdIndex = this._messageIds.indexOf(out.headers.messageId);
    if (messageIdIndex > -1) {
      out.self = true;
      this._messageIds = this._messageIds.splice(messageIdIndex, 1);
    }
    return out;
  };

  _socketListener = (frame) => {
    console.log('Connected: ' + frame);
    this._socket.stomp.subscribe(
      this._options.topicName,
      data => this.subscribers.forEach(subscriber => subscriber.observer.next(this.getMessage(data)))
    );
  }

  _onSocketError = (errorMsg) => {
    const out: any = {};
    out.type = 'ERROR';
    out.message = errorMsg;
    this.subscribers.forEach(subscriber => subscriber.observer.error(out));
    this.scheduleReconnection();
  }

  scheduleReconnection = () => {
    this.reconnectionPromise = setTimeout(() => {
      console.log('Socket reconnecting... (if it fails, next attempt in ' + this._options.reconnectionTimeout + ' msec)');
      this.connect();
    }, this._options.reconnectionTimeout);
  }

  reconnectNow = function () {
    this._socket.stomp.disconnect();
    if (this.reconnectionPromise && this.reconnectionPromise.cancel) {
      this.reconnectionPromise.cancel();
    }
    this.connect();
  };

  send = (message) => {
    const id = Math.floor(Math.random() * 1000000);
    this._socket.stomp.send(this._options.brokerName, {
      priority: 9
    }, JSON.stringify({
      message: message,
      id: id
    }));
    this._messageIds.push(id);
  };

  connect = () => {
    const headers = {};

    let socketUrl = this._options.socketUrl;
    if (this._options.getAccessToken()) {
      socketUrl += `?access_token=${this._options.getAccessToken()}`;
    }

    this._socket.client = new SockJS(socketUrl);
    this._socket.stomp = Stomp.over(this._socket.client);
    this._socket.stomp.connect(headers, this._socketListener, this._onSocketError);
    this._socket.stomp.onclose = this.scheduleReconnection;
  }


}
