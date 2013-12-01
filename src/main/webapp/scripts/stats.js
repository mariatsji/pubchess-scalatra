// http://localhost:7002/players/stats/52927a0e03ce66b5d1f6fc4e
//http://localhost:7002/stats.html?id=52927a0e03ce66b5d1f6fc4e
// {"_id":"529638a603ce4c071c0f2077","playerid":"52927a0e03ce66b5d1f6fc4e","date":"2013-11-27T18:23:34Z","elo":1185.0}
var stats = {

    sortElos: function(eloa, elob) {
        return eloa.elo - elob.elo;
    },

    minElo: function(elos) {
        if(elos.length > 0) {
            elos.sort(this.sortElos);
            return elos[0];
        } else {
            return 0;
        }
    },

    maxElo: function(elos) {
        if(elos.length > 0) {
            elos.sort(sortElos).reverse;
            return elos[0];
        } else {
            return 0;
        }
    },

    datify: function(dateString) {
        var parts = dateString.split('Z');
        var date1 = dateString[0];
        var time1 = dateString[1];
        var dateparts = date1.split('-');
        var timeparts = time1.split(':');
        return new Date(dateparts[0], dateparts[1], dateparts[2], timeparts[0], timeparts[1], timeparts[2]);
    }

    sortDates: function(eloa, elob) {
        if(datify(eloa.date) < datify(elob.date)) {
            return -1;
        } else {
            return 1;
        }
    },

    minDate: function(elos) {
        if(elos.length > 0) {
            dates.sort(sortDates);
            return elos[0];
        } else {
            return 0;
        }
    },

    maxDate: function(elos) {
        if(elos.length > 0) {
            elos.sort(sortDates).reverse;
            return elos[0];
        } else {
            return 0;
        }
    },

};