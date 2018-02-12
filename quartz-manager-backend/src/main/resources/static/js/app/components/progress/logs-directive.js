angular.module('progress')
.directive('logsPanel', ['LogService', function(LogService){
	
	var MAX_LOGS = 10;
	
	return{
		restrict: 'E',
		controller : ['$scope', '$rootScope', '$http', function($scope, $rootScope, $http){
			
			$scope.logs = new Array();
			
			var _showNewLog = function(logRecord){
				if($scope.logs.length > MAX_LOGS)
					$scope.logs.pop();
				
				var logItem = {};
				logItem.time = logRecord.date;
				logItem.type = logRecord.type;
				logItem.msg = logRecord.message;
				logItem.threadName = logRecord.threadName;
				
				$scope.logs.unshift(logItem);
			};
			
			var _refreshSession = function(){
				$http({
					method : 'GET',
					url : 'session/refresh'
				});
			};
			
			var _handleNewMsgFromLogWebsocket = function(receivedMsg){
				if(receivedMsg.type == 'SUCCESS')
					_showNewLo	g(receivedMsg.message);
				else if(receivedMsg.type == 'ERROR')
					_refreshSession(); //if websocket has been closed for session expiration, try to refresh it
			};
			
			
			LogService.receive().then(null, null, function(receivedMsg){
				_handleNewMsgFromLogWebsocket(receivedMsg);
			});
			
			$rootScope.$on('event:auth-loginConfirmed', function() {
				//REST API login succeeded, now open websocket connection again
				LogService.reconnectNow();
		    });
		}],
		templateUrl : 'templates/manager/logs-panel.html'
	};
}]);