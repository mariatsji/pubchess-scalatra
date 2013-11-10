app.factory('dataService', function($http) {
    return {
        getAllPlayers: function() {
            return $http.get('/players').then(function(resp) {
                return resp.data;
            });

        },
        addPlayer: function(player) {
            return $http.post('/players', player).then(function(resp) {
                return resp.data;
            });
        },
        getPlayer: function(id) {
            return $http.get('/players/' + id).then(function(resp) {
                return resp.data;
            });

        },
        deletePlayer: function(id) {
            return $http.delete('/players/' + id).then(function(resp) {
                return resp.data;
            })
        },
        updatePlayer: function(player) {
            return $http.put('/players/' + player._id, player).then(function(resp) {
                return resp.data;
            })
        }
    }
});
