// http://localhost:7002/players/stats/52927a0e03ce66b5d1f6fc4e
//http://localhost:7002/stats.html?id=52927a0e03ce66b5d1f6fc4e
// {"_id":"529638a603ce4c071c0f2077","playerid":"52927a0e03ce66b5d1f6fc4e","date":"2013-11-27T18:23:34Z","elo":1185.0}
var stats = {

    sortElos: function(eloa, elob) {
        return eloa.elo - elob.elo;
    },

    minElo: function(elos) {
        if(elos.length > 0) {
            return elos.sort(this.sortElos)[0];
        } else {
            return 0;
        }
    },

    maxElo: function(elos) {
        if(elos.length > 0) {
            return elos.sort(stats.sortElos).reverse()[0];
        } else {
            return 0;
        }
    },

    datify: function(dateString) {
        var parts = dateString.split('Z')[0].split('T');
        var date1 = parts[0];
        var time1 = parts[1];
        var dateparts = date1.split('-');
        var timeparts = time1.split(':');
        return new Date(dateparts[0], dateparts[1], dateparts[2], timeparts[0], timeparts[1], timeparts[2]);
    },

    sortDates: function(eloa, elob) {
        var a = stats.datify(eloa.date);
        var b = stats.datify(elob.date);
        if(a.getTime() < b.getTime()) {
            return -1;
        } else {
            return 1;
        }
    },

    minDate: function(elos) {
        if(elos.length > 0) {
            return elos.sort(stats.sortDates)[0];
        } else {
            return 0;
        }
    },

    maxDate: function(elos) {
        if(elos.length > 0) {
            return elos.sort(stats.sortDates).reverse()[0];
        } else {
            return 0;
        }
    }

};