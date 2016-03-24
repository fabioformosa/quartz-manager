angular.module('progress')
.directive('progressPanel', function(){
	
	return{
		restrict: 'E',
		controller : ['$scope', '$http', function($scope, $http){
			$http.get('scheduler/progress').then(function(res){
				$scope.progress = res.data;
				$scope.percentageStr = $scope.progress.percentage + '%';
			});
		}],
		templateUrl : 'templates/manager/progress-panel.html'
	};
});