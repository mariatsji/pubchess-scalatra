function PubchessController($scope) {
    $scope.players = [];
    $http.get('/players').
    then(function (result) {
        $scope.players = result.data;
    });
}
