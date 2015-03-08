angular
    .module('endmundApp', ['frapontillo.gage'])
    .controller('EndmundCtrl', function ($scope, $http) {

    $scope.results = [];
    $scope.correctSolutions = 0;

    var socket = new SockJS('/solution');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
         console.log('Connected: ' + frame);
         stompClient.subscribe('/topic/solutions', function(edmundSolution){
                  var aSolution = angular.fromJson(edmundSolution.body)
                  $scope.results.push(aSolution);
                  if(aSolution.edmundCorrect) {
                    $scope.correctSolutions++;
                  }
                  $scope.value = ($scope.correctSolutions / $scope.results.length) * 100;
                  $scope.gaugeColor = '#e74c3c';
                  $scope.$apply();
         });
    });

    $http.get('/endmund/clues').
       success(function(data, status, headers, config) {
         $scope.results = data;

         $scope.results.forEach(function (entry) {
            if(entry.edmundCorrect) {
                $scope.correctSolutions++;
                $scope.value = ($scope.correctSolutions / $scope.results.length) * 100;
            }
         })
         $scope.gaugeColor = '#e74c3c';
    });


    $scope.value = 0;
    $scope.valueFontColor = 'white';
    $scope.min = 0;
    $scope.max = 100;
    $scope.showInnerShadow = true;
    $scope.noGradient = true;
    $scope.label = 'Successful Solutions';
    $scope.labelFontColor = 'white';
    $scope.startAnimationTime = 0;
    $scope.counter = true;
    $scope.gaugeColor = '#e74c3c';
    $scope.levelGradient = true;
    $scope.levelColors = ['#2ecc71'];
    $scope.hideMinMax = true;
    $scope.symbol = '%';

    });