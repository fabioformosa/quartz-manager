angular.module('starter')
.directive('starterBtn', function(){
	
	return{
		restrict: 'EA',
		scope : {
			schedulerState : '@initState'
		},
		controller : ['$scope', '$http', function($scope, $http){
			
			var startScheduler = function(){
				var onStartingSuccess = function(res){
					$scope.schedulerState = 'running';
				};
				var onStartingError = function(res){
					console.log(JSON.stringify(res));
				};
				$http.get('scheduler/run').then(onStartingSuccess, onStartingError);
			};

			var stopScheduler = function(){
				var onStoppingSuccess = function(res){
					$scope.schedulerState = 'stopped';
				};
				var onStoppingError = function(res){
					console.log(JSON.stringify(res));
				};
				$http.get('scheduler/stop').then(onStoppingSuccess, onStoppingError);
			};
			
			var pauseScheduler = function(){
				var onSuccess = function(res){
					$scope.schedulerState = 'paused';
				};
				var onError = function(res){
					console.log(JSON.stringify(res));
				};
				$http.get('scheduler/pause').then(onSuccess, onError);
			};
			
			var resumeScheduler = function(){
				var onSuccess = function(res){
					$scope.schedulerState = 'running';
				};
				var onError = function(res){
					console.log(JSON.stringify(res));
				};
				$http.get('scheduler/resume').then(onSuccess, onError);
			};
			
			$scope.stop = function(){
					if($scope.schedulerState != 'stopped')
						stopScheduler();
			};
			
			$scope.startOrPause = function(){
				switch ($scope.schedulerState) {
				case 'running':	pauseScheduler();
								break;
				case 'paused':	resumeScheduler();
								break;
				default:
					startScheduler();
					break;
				}
			};
			
		}],
		template: 
				'<button class="btn btn-default large-btn" ng-click="startOrPause()">'
				+ 	'<i id="schedulerControllerBtn1"'
				+ 		'class="fa fa-2x"'
				+		'ng-class="{\'fa-pause red\': schedulerState == \'running\', \'fa-play green\': schedulerState ==\'stopped\' || schedulerState == \'paused\'}">'
		        + 	'</i>'
		        + '</button>'
//		        + '<button class="btn btn-default large-btn" ng-disabled="schedulerState == \'stopped\'" ng-click="stop()">'
//		        + 	'<i id="schedulerControllerBtn2"'
//		        + 		'class="fa fa-2x fa-stop red">' 
//		        + 	'</i>'
//		        + '</button>'
	};
});