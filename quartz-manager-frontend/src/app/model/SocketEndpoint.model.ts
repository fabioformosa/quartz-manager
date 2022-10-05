import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export class SocketEndpoint {
  client: SockJS;
  stomp: Stomp;
}
