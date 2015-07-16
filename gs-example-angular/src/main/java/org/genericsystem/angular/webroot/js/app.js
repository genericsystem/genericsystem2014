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
		//alert(JSON.stringify(data));
		$scope.choices = data;
		$scope.select = function(choice){
			path = choice.tableName;
			columns = choice.columns;
			
			$scope.activePath = $location.path('/list');			
		};		
	});
}

function ListCtrl($scope, $http) {

	// affichage global
	$http.get('/api/'+path).success(function (data) {
		$scope.instances = data;		
		$scope.layout = path;		
		$scope.names = columns;
		
		
	}); 

	// persistance
	$scope.commit = function(inst){

		$http.put('/api/'+angular.$scope, instance).succes(function (data) {
			$scope.instances = data;
			$scope.activePath = $location.path('/list');
		});
		$scope.reset = function () {
			$scope.instance = angular.copy($scope.master);
		};

		$scope.reset();
	};

//	shift
	$scope.shift = function (instance) {
		var deleteInst = confirm('Are you absolutely sure you want to shift?');
		if (deleteInst) {
			$http.delete('/api/'+path+'/shift/');
			$scope.activePath = $location.path('/list');
		}
	}; 

//	clear
	$scope.clear = function (instance) {
		var deleteInst = confirm('Are you absolutely sure you want to delete?');
		if (deleteInst) {
			$http.delete('/api/'+path+'/clear/');
			$scope.activePath = $location.path('/list');
		}
	}; 
}

function AddCtrl($scope, $http, $location) {

	$scope.master = {};
	$scope.activePath = null;
	$scope.layout = path;
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
	$scope.layout = path;	
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
