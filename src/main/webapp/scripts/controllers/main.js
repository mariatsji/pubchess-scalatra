'use strict';

function MainController($scope, dataService) {
    $scope.showError = false;

    dataService.getAllPlayers().then(
        function(players) {
            $scope.players = players;
        },
        function() {
            showError('feil ved henting av spillere');
        }
    );

    function showError(message) {
        $scope.showError = true;
        $scope.errorMessage = message;
    }

    $scope.submitPlayer = function(player) {
        dataService.addPlayer(player).then(
            function() {
                $scope.players.push(player);
            },
            function() {
                showError('feil ved lagring av spiller');
            }
        );
    }
};
