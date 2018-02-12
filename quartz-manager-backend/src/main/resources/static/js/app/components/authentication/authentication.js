'use strict';
var authenticationComp = angular.module('authenticationComp', ['http-auth-interceptor']);


/**********************************************************************************/
//SESSION REFRESH
authenticationComp.directive('sessionRefresh', 
	['authService','$rootScope', '$http', '$location', 
	 'LoginService',  function(authService, $rootScope, $http, $location, LoginService) {
var openedLoginDialog = false;
return{
	restrict: 'AC',
	link: function(scope, elem, attrs) {

		var lastRefreshTimeForAnonymous;
					
		$rootScope.$on('event:auth-loginRequired', function(e, arg) {
			//show login dialog
			arg.ajaxLoginError = arg.ajaxLoginError || "";
			
			attrs.title = attrs.title || "Session Expired";
			attrs.commonErrorLabel = attrs.commonErrorLabel || "Authenticaion failed:";
			attrs.wrongPasswordMsg = attrs.wrongPasswordMsg || "Wrong Password! Please, re-try.";
			attrs.serverConnectionFailedMsg = attrs.serverConnectionFailedMsg || "Server connection failed, please try later";
			
			var username = attrs.username || "admin";
			
			if(openedLoginDialog)
				return;
			
			openedLoginDialog = true;
			bootbox.dialog({
				message: "<div id=\"refreshSessionDialog\">" +
							 "<h4 id=\"refreshSessionHeader\">Login</h4>"+
							 "<span id=\"refreshSessionError\">" + arg.ajaxLoginError + " </span>"+
							 "<table id=\"refreshSessionTable\">" +
							 	"<tr>" +
							 		"<label for=\"username\" class=\"block clearfix\">" +
							 			"<span class=\"block input-icon input-icon-right\">" +
							 				"<input id=\"ajaxLoginUsername\" name=\"ajaxLoginUsername\" class=\"form-control\" type=\"text\" value=\"" + username + "\" readonly=\"true\" size=\"35\">" +
							 			"</span>" +
							 		"</label>" +
							 	"</tr>" +
							 	"<tr>" +
								 	"<label for=\"username\" class=\"block clearfix\">" +
							 			"<span class=\"block input-icon input-icon-right\">" +
							 				"<input id=\"ajaxLoginPassword\" name=\"ajaxLoginPassword\" class=\"form-control\" type=\"password\" placeholder=\"password\" size=\"35\">" +
							 			"</span>" +
						 			"</label>"+
							 	"</tr>" +
							 "</table>" +
						 "</div>",		
				title: attrs.title,
				onEscape: function() {
					openedLoginDialog = false;
				 },
				buttons:{
					main:{
						label: "Ok",
					      className: "btn-primary",
					      callback: function() {
					    	  openedLoginDialog = false;
						       var postData = {};
						       postData.ajaxUsername = $('#ajaxLoginUsername').val();
						       postData.ajaxPassword = $('#ajaxLoginPassword').val();
						       
						       var loginCompleted = LoginService.doLogin(postData.ajaxUsername, postData.ajaxPassword, true);
						       
						       loginCompleted
						       .then(function(){
						    	   authService.loginConfirmed();
						       },function(msgError){
						    	   $rootScope.$emit('event:auth-loginRequired', {ajaxLoginError: msgError});
						       });
					      }
					},
				},
			});
			
			$('#refreshSessionDialog').closest('.modal').css('z-index', '9001');
		});
		
		$rootScope.$on('event:auth-loginConfirmed', function() {
        	//nothing
	        });
			
		}
	};
}]);

/**********************************************************************************/
//LOGIN SERVICE
authenticationComp.service('LoginService', ['$q', '$http', '$window', '$log', function($q, $http, $window, $log){

	this.doLogin = function(username, password, ignoreAuthModule){
		ignoreAuthModule = ignoreAuthModule || false;
		
		var ajaxLoginDone = $q.defer(); 
		
		var postData = {};
	    postData.ajaxUsername = username;
	    postData.ajaxPassword = password;
  
		$http({
		  	   method: 'POST',
		  	   url: 'ajaxLogin',
		  	   headers: {'Content-Type': 'application/x-www-form-urlencoded'},
		  	   transformRequest: function(obj) {
		  	        var str = [];
		  	        for(var p in obj)
		  	        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
		  	        return str.join("&");
		  	    },
		  	    data: postData,
		  	    ignoreAuthModule : ignoreAuthModule
		})								       							       
	     .then(function (res) {
		  	   if(res.status == 200){
		  		   for(var i in res.cookies)
		  			   if(res.cookies[i] != null)
		  				   document.cookie = res.cookies[i];
		  		   ajaxLoginDone.resolve(res.loggedUser);
		  	   }
		  	   else
			       ajaxLoginDone.reject(res.msgError);
		     }, function(data, status){
		    	   ajaxLoginDone.reject();
		       });
			
			  return ajaxLoginDone.promise;
	};
	
	
}]);
