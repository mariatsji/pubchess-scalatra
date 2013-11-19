app.factory('dataService', function($http)  {
   return {
       getAllPlayers: function() {
           return $http.get('/players').then(function(resp) {
               return resp.data;
           });
       }
   }
});