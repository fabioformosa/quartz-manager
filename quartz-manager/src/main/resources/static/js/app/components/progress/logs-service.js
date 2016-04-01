angular.module('progress')
.service('LogService',['WebsocketServiceFactory', function(WebsocketServiceFactory){

	var logServiceParams = {
			SOCKET_URL  : '/quartz-manager/logs',
			TOPIC_NAME  : '/topic/logs'
	};
	
	return WebsocketServiceFactory.create(logServiceParams);
}]);