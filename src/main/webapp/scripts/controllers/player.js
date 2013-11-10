'use strict';

function PlayerController($scope, $routeParams, dataService) {
    $scope.showError = false;
    $scope.player = dataService.getPlayer($routeParams.id).then(
        function(player) {
            $scope.player = player;
            $scope.comments = player.comments;
        },
        function() {
            showError('feil ved henting av spiller');
        }
    );

    $scope.hideError = function() {
        $scope.showError = false;
    }

    function showError(message) {
        $scope.showError = true;
        $scope.errorMessage = message;
    }

    $scope.showPlayerEdit = false;
    $scope.toggleShowPlayerEdit = function() {
        $scope.showPlayerEdit = !$scope.showPlayerEdit;
    }

    $scope.deletePlayer = function(player) {
        dataService.deletePlayer(player._id).then(
            function() {

            },
            function() {
                showError('feil ved sletting av spiller');
            }
        );
    }

    $scope.updatePlayer = function(player) {
        dataService.updatePlayer(player).then(
            function() {
//                _.find($scope.players, function(current) {
//                    return current.id == player.id;
//                })
            },
            function() {
                showError('feil ved oppdatering av spiller');
            }
        );
    }
};
