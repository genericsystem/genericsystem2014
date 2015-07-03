angular.module('CrudApp', []).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/', {templateUrl: '/tpl/lists.html', controller: ListCtrl}).
        when('/add-user', {templateUrl: '/tpl/add-new.html', controller: AddCtrl}).
        when('/edit/:id', {templateUrl: '/tpl/edit.html', controller: EditCtrl}).
        otherwise({redirectTo: '/'});
   
}]);

function ListCtrl($scope, $http) {
	
    $http.get('/api/users').success(function (data) {
    	
        $scope.cars = data;
    });
    
    $scope.commit = function(car){
    	
        $http.put('/api/users/', car).succes(function (data) {
        $scope.car = data;
         $scope.activePath = $location.path('/');
     });
 $scope.reset = function () {
 $scope.car = angular.copy($scope.master);
 };

 $scope.reset();
         }
    
    $scope.clear = function (car) {
        var deleteCars = confirm('Are you absolutely sure you want to delete?');
        if (deleteCars) {
            $http.delete('/api/users/');
            $scope.activePath = $location.path('/');
        }
    }; 
}

function AddCtrl($scope, $http, $location) {
	
    $scope.master = {};
    $scope.activePath = null;

    $scope.add_new = function (car, AddNewForm) {

        $http.post('/api/users', car).success(function () {
            $scope.reset();
            $scope.activePath = $location.path('/');
        });
  
        $scope.reset = function () {
            $scope.car = angular.copy($scope.master);
        };

        $scope.reset();
    };
}

function EditCtrl($scope, $http, $location, $routeParams) {
    var id = $routeParams.id;    
    $scope.activePath = null;

    $http.get('/api/users/' + id).success(function (data) {
        $scope.car = data;
    });

    $scope.update = function (car) {
        $http.put('/api/users/' + id, car).success(function (data) {
            $scope.car = data;
            $scope.activePath = $location.path('/');
        });
    };

    $scope.delete = function (user) {
        var deleteCar = confirm('Are you absolutely sure you want to delete?');
        if (deleteCar) {
            $http.delete('/api/users/' + car.id);
            $scope.activePath = $location.path('/');
        }
    };
}