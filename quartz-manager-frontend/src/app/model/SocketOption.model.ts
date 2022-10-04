export class SocketOption {
  socketUrl: string;
  topicName: string;
  brokerName: string;
  reconnectionTimeout = 30000

  getAccessToken: Function = () => null;

  constructor(socketUrl: string,
              topicName: string,
              getAccessToken?: Function,
              brokerName: string = null,
              reconnectionTimeout: number = 30000) {
    this.socketUrl = socketUrl;
    this.topicName = topicName;
    this.brokerName = brokerName;
    this.reconnectionTimeout = reconnectionTimeout;
    this.getAccessToken = getAccessToken || (() => null);
  }
}
