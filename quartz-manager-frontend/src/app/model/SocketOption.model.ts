export class SocketOption{
    socketUrl  : string;
    topicName  : string; 
    brokerName : string;
    reconnectionTimeout : number = 30000

    constructor(socketUrl : string, topicName : string, brokerName : string = null, reconnectionTimeout : number = 30000){
        this.socketUrl = socketUrl;
        this.topicName = topicName;
        this.brokerName = brokerName;
        this.reconnectionTimeout = reconnectionTimeout;
    }

}