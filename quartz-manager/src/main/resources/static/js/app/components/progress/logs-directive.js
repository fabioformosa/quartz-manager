angular.module('progress')
.directive('logsPanel', ['LogService', function(LogService){
	
	var appendLog = function(log){
		var logTable = document.getElementById('logTable');
        var row = document.createElement('tr');
        row.style.wordWrap = 'break-word';
        row.appendChild(document.createTextNode(log));
        logTable.appendChild(row);
	}
	
	var MAX_LOGS = 10;
	
	return{
		restrict: 'E',
		controller : ['$scope', function($scope){
			
			$scope.logs = new Array();
			
			LogService.receive().then(null, null, function(logMsg){
				if($scope.logs.length > MAX_LOGS)
					$scope.logs.pop();
				
				var logItem = {};
				logItem.time = new Date();
				logItem.msg = logMsg;
				
				$scope.logs.unshift(logItem);
			})
		}],
		templateUrl : 'templates/manager/logs-panel.html'
	};
}]);