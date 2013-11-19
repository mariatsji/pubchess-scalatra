function PubchessController($scope, $routeParams, dataService) {

    $scope.players = dataService.getAllPlayers().then(
      function(players) {
          $scope.players = players;
      }  ,
      function() {
          showError('feil ved henting av spiller');
      }
    );


}
