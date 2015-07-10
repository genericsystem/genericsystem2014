angular.module('CrudApp', []).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/', {templateUrl: '/tpl/lists.html', controller: ListCtrl}).
        when('/add-car', {templateUrl: '/tpl/add-new.html', controller: AddCtrl}).
        when('/edit/:id', {templateUrl: '/tpl/edit.html', controller: EditCtrl}).
        otherwise({redirectTo: '/'});
   
}]);

function ListCtrl($scope, $http) {
	
	//affichage global
    $http.get('/api/cars').success(function (data) {
    	
        $scope.cars = data;
       
    });
    
    
    //persistance
    $scope.commit = function(car){
    	
        $http.put('/api/cars/', car).succes(function (data) {
        $scope.car = data;
         $scope.activePath = $location.path('/');
     });
 $scope.reset = function () {
 $scope.car = angular.copy($scope.master);
 };

 $scope.reset();
         }
    
    //shift
    $scope.shift = function (car) {
        var deleteCars = confirm('Are you absolutely sure you want to shift?');
        if (deleteCars) {
            $http.delete('/api/cars/shift/');
            $scope.activePath = $location.path('/');
        }
    }; 
    
    //clear
    $scope.clear = function (car) {
        var deleteCars = confirm('Are you absolutely sure you want to delete?');
        if (deleteCars) {
            $http.delete('/api/cars/clear/');
            $scope.activePath = $location.path('/');
        }
    }; 
}

function AddCtrl($scope, $http, $location) {
	
    $scope.master = {};
    $scope.activePath = null;

    $scope.add_new = function (car, AddNewForm) {

        $http.post('/api/cars', car).success(function () {
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

    $http.get('/api/cars/' + id).success(function (data) {
        $scope.car = data;
    });

    $scope.update = function (car) {
        $http.put('/api/cars/' + id, car).success(function (data) {
            $scope.car = data;
            $scope.activePath = $location.path('/');
        });
    };

    $scope.delete = function (car) {
        var deleteCar = confirm('Are you absolutely sure you want to delete?');
        if (deleteCar) {
            $http.delete('/api/cars/' + car.id);           
            $scope.activePath = $location.path('/');
        }
    };
}