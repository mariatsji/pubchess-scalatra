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
    },

    line: function(x1, y1, x2, y2) {
        var line = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line.setAttribute('x1', x1);
        line.setAttribute('y1', y1);
        line.setAttribute('x2', x2);
        line.setAttribute('y2', y2);
        line.setAttribute('style', 'stroke:rgb(0,0,0);stroke-width:3');
        return line;
    },

    text: function(textstring, x, y) {
        var text = document.createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttribute('x', x);
        text.setAttribute('y', y);
        text.setAttribute('fill', 'black');
        text.appendChild(document.createTextNode(textstring));
        return text;
    },

    striptime: function(datestring) {
        return datestring.split('T')[0];
    },

    nextDestination: function(height, width, elos, index, minelo, maxelo) {
        var x = stats.toX(elos[index], height, minelo, maxelo);
        var y = stats.toY(elos, width, index);
        var retVal = new Array();
        retVal[0] = x;
        retVal[1] = y;
        return retVal;
    },

    toX: function(elo, height, minElo, maxElo) {
        var heightPrElo = (height/(maxElo.elo - minElo.elo)).toFixed(0);
        var retVal = (height - (heightPrElo / elo.elo)).toFixed(0);
        return retVal;
    },

    toY: function(elos, width, index) {
        return (elos.length / width).toFixed(0) * index;
    }


};