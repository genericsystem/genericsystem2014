angular.module('CrudApp', []).config(['$routeProvider', function ($routeProvider) {
	$routeProvider.
	when('/', {templateUrl: '/tpl/home.html', controller: IndexCtrl}).
	when('/list', {templateUrl: '/tpl/lists.html', controller: ListCtrl}).
	when('/add-inst', {templateUrl: '/tpl/add-new.html', controller: AddCtrl}).
	when('/edit/:id', {templateUrl: '/tpl/edit.html', controller: EditCtrl}).
	otherwise({redirectTo: '/'});

}]);

function IndexCtrl($scope, $http, $location){

	$http.get('/api/types').success(function(data){	
		$scope.choices = data;		
		$scope.select = function(choice){
			path = choice.tableName;
			columns = choice.columns;			
			$scope.activePath = $location.path('/list');			
		};		
	});
}

function ListCtrl($scope, $http) {
	$scope.type = path;
	$scope.names = columns;
	
	// affichage global	
	$http.get('/api/'+path).success(function (data) {			
		$scope.instances = data;		
	});	
	$scope.loadData = function(){
		$http.get('/api/'+path).success(function (data) {			
			$scope.instances = data;		
		});
	};	
	// persist
	$scope.commit = function(instance){
		$http.put('/api/'+path, instance).succes(function (data) {
			$scope.instances = data;
			$scope.activePath = $location.path('/list');
		});
	};
//	shift
	$scope.shift = function (instance) {
		var deleteInst = confirm('Are you absolutely sure you want to shift?');
		if (deleteInst) {
			$http.delete('/api/'+path+'/shift/');
			$scope.loadData();
		}		
	}; 
//	clear
	$scope.clear = function (instance) {
		var deleteInst = confirm('Are you absolutely sure you want to delete?');
		if (deleteInst) {
			$http.delete('/api/'+path+'/clear/');
			$scope.loadData();
		}			
	};	
}

function AddCtrl($scope, $http, $location) {
	$scope.master = {};
	$scope.activePath = null;
	$scope.type = path;
	$scope.names = columns;
	$scope.add_new = function (instance, AddNewForm) {
		$http.post('/api/'+path+'/', instance).success(function () {
			$scope.reset();
			$scope.activePath = $location.path('/list');
		});
		$scope.reset = function () {
			$scope.instance = angular.copy($scope.master);
		};
		$scope.reset();
	};
}

function EditCtrl($scope, $http, $location, $routeParams) {
	var id = $routeParams.id; 
	$scope.activePath = null;
	$scope.type = path;	
	$scope.names = columns; 
	$http.get('/api/'+path+'/' + id).success(function (data) {
		$scope.instance = data;
	});

	$scope.update = function (instance) {
		$http.put('/api/'+path+'/' + id, instance).success(function (data) {
			$scope.instance = data;
			$scope.activePath = $location.path('/list');
		});
	};

	$scope.delete = function (instance) {
		var deleteInst = confirm('Are you absolutely sure you want to delete?');
		if (deleteInst) {
			$http.delete('/api/'+path+'/' + instance.id);           
			$scope.activePath = $location.path('/list');
		}
	};
}
