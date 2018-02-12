angular.module('progress')
.service('ProgressService',['WebsocketServiceFactory', function(WebsocketServiceFactory){

	var progressServiceParams = {
			SOCKET_URL  : '/quartz-manager/progress',
			TOPIC_NAME  : '/topic/progress'
	};
	
	return WebsocketServiceFactory.create(progressServiceParams);
}]);