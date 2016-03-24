angular.module('configurator')
.directive('configForm', function(){
	
	var configBackup = {triggerPerDay : '', maxCount : ''};
	
	return{
		restrict: 'E',
		controller : ['$scope', '$http', function($scope, $http){
			$http.get('scheduler/config').then(function(res){
				$scope.config = res.data;
				configBackup = res.data;
			});
			
			$scope.submitConfig = function (){
				$http.post('scheduler/config', $scope.config)
					.then(function(res){
						configBackup = $scope.config;
					}, function(error){
						$scope.congif = configBackup;
					});
			};
			
		}],
		templateUrl : 'templates/manager/config-form.html',
		link : function($scope){
		}
	};
});