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
			$scope.activePath = null;		
			// place le type sélectionné dans le scope angular
			angular.$scope = choice.type;			
			$scope.activePath = $location.path('/list');

		};

	});
}

function ListCtrl($scope, $http) {

	// affichage global
	$http.get('/api/'+angular.$scope).success(function (data) {

		$scope.instances = data;		
		$scope.layout = angular.$scope;
		// permet d'obtenir les clés des objets Json récupérés par la requête.
		var keys = Object.keys(data[0]);
		$scope.colNames = keys;  

	}); 
  

// persistance
$scope.commit = function(inst){

	$http.put('/api/'+angular.$scope, inst).succes(function (data) {
		$scope.instances = data;
		$scope.activePath = $location.path('/list');
	});
	$scope.reset = function () {
		$scope.inst = angular.copy($scope.master);
	};

	$scope.reset();
};

// shift
$scope.shift = function (inst) {
	var deleteInst = confirm('Are you absolutely sure you want to shift?');
	if (deleteInst) {
		$http.delete('/api/'+angular.$scope+'/shift/');
		$scope.activePath = $location.path('/list');
	}
}; 

// clear
$scope.clear = function (inst) {
	var deleteInst = confirm('Are you absolutely sure you want to delete?');
	if (deleteInst) {
		$http.delete('/api/'+angular.$scope+'/clear/');
		$scope.activePath = $location.path('/list');
	}
}; 
}

function AddCtrl($scope, $http, $location) {

	$scope.master = {};
	$scope.activePath = null;
	$scope.layout = angular.$scope;

	$scope.add_new = function (inst, AddNewForm) {

		$http.post('/api/'+angular.$scope+'/', inst).success(function () {
			$scope.reset();
			$scope.activePath = $location.path('/list');
		});

		$scope.reset = function () {
			$scope.inst = angular.copy($scope.master);
		};

		$scope.reset();
	};
}

function EditCtrl($scope, $http, $location, $routeParams) {
	var id = $routeParams.id;    
	$scope.activePath = null;
	$scope.layout = angular.$scope;
	$http.get('/api/'+angular.$scope+'/' + id).success(function (data) {
		$scope.inst = data;
	});

	$scope.update = function (car) {
		$http.put('/api/'+angular.$scope+'/' + id, inst).success(function (data) {
			$scope.inst = data;
			$scope.activePath = $location.path('/list');
		});
	};

	$scope.delete = function (inst) {
		var deleteInst = confirm('Are you absolutely sure you want to delete?');
		if (deleteInst) {
			$http.delete('/api/'+angular.$scope+'/' + inst.id);           
			$scope.activePath = $location.path('/list');
		}
	};
}
