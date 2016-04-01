angular.module('progress')
.directive('progressPanel',['ProgressService', function(ProgressService){
	
	return{
		restrict: 'E',
		controller : ['$scope', function($scope){
			
			ProgressService.receive().then(null, null, function(newStatus){
				$scope.progress = newStatus;
				$scope.percentageStr = $scope.progress.percentage + '%';
			});
			
		}],
		templateUrl : 'templates/manager/progress-panel.html'
	};
}]);