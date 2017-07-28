angular.module('progress')
	.directive('progressPanel', [ 'ProgressService', function(ProgressService) {

		return {
			restrict : 'E',
			controller : [ '$scope', '$rootScope', function($scope, $rootScope) {

				ProgressService.receive().then(null, null, function(receivedMsg) {
					if (receivedMsg.type == 'SUCCESS') {
						var newStatus = receivedMsg.message;
						$scope.progress = newStatus;
						$scope.percentageStr = $scope.progress.percentage + '%';
					}
				});

				$rootScope.$on('event:auth-loginConfirmed', function() {
					//re-open websocket connection after REST API login
					ProgressService.reconnectNow();
				});

			}],
			
			templateUrl : 'templates/manager/progress-panel.html'
		};
	} ]);