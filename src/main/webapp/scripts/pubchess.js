var result_white_won = 1;
var result_black_won = 2;
var result_draw = 3;

function getPlayer(playerid) {
    var player = httpGet('/players/' + playerid);
    return JSON.parse(player);
}

function getPlayers() {
    asyncGet('/players', getPlayersCallback);
}

function getMatch(matchid) {
    var match = httpGet('/matches/' + matchid);
    return JSON.parse(match);
}

function getTournaments() {
    var tournaments = httpGet('/tournaments');
    return JSON.parse(tournaments);
}

function getTournament(tournamentid) {
    var tournament = httpGet('/tournaments/' + tournamentid);
    return JSON.parse(tournament);
}

function httpGet(theUrl) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open('GET', theUrl, false);
    xmlHttp.send(null);
    return xmlHttp.responseText;
}

function asyncGet(theUrl, callbackFunction) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = callbackFunction;
    xmlHttp.open('GET', theUrl, true);
    xmlHttp.send(null);
}

function httpPost(theUrl, json) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open('POST', theUrl, true);
    xmlHttp.setRequestHeader('Content-type', 'application/json; charset=UTF-8');
    xmlHttp.send(json);
    xmlHttp.onloadend = function () {
        console.log('completed sending new player');
    }
}

function httpPut(theUrl, json) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open('PUT', theUrl, true);
    xmlHttp.setRequestHeader('Content-type', 'application/json; charset=UTF-8');
    xmlHttp.send(json);
    xmlHttp.onloadend = function () {
        console.log('completed sending new player');
    }
}

function addPlayer(name) {
    var player = {};
    player ['name'] = name;
    player ['elo'] = '1200';
    var json = JSON.stringify(player);

    httpPost('/players', json)
}

function createTournamentObject(name, participants) {
    var length = participants.length;
    var tournament = {
        name: name,
        playerids: [],
        matchids: []
    };

    for (var i = 0 ; i < length ; i++) {
        var thisid = participants[i].value;
        if(participants[i].checked == true) {
            tournament.playerids.push(thisid);
        }
    }
    return tournament;
}

function addTournamentSingle(name, participants) {
    var tournament = createTournamentObject(name, participants);
    var json = JSON.stringify(tournament);
    httpPost('/tournaments/single', json);
}

function addTournamentDouble(name, participants) {
    var tournament = createTournamentObject(name, participants);
    var json = JSON.stringify(tournament);
    httpPost('/tournaments/double', json);
}

function getRequestParameter(name) {
    var retString = 'not found';
    if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search)) {
        retString = decodeURIComponent(name[1]);
    }
    return retString;

}

function printMatchResult(match) {
    var retString = '-';

    if(match.result == result_white_won) {
        retString = 'White won';
    } else if (match.result == result_black_won) {
        retString =  'Black won';
    } else if (match.result == result_draw) {
        retString = 'Remis';
    }
    return retString;
}

function printMatchLI(match) {
    return '<li><strong>' + getPlayer(match.white_id).name + ' vs ' + getPlayer(match.black_id).name + '</strong> ' +  printMatchResultButtons(match) + ' ' + printMatchResult(match) + '</li>';
}

function printMatchResultButtons(match) {
    return '<button type=\"button\" onClick=\"saveMatchResult(\'' + match._id + '\',' + result_white_won +');\">White won</button>' +
        '<button type=\"button\" onClick=\"saveMatchResult(\'' + match._id + '\',' + result_draw +');\">Remis</button>' +
        '<button type=\"button\" onClick=\"saveMatchResult(\'' + match._id + '\',' + result_black_won +');\">Black won</button>';
}

function saveMatchResult(matchid, result) {
    var fullMatch = getMatch(matchid);
    fullMatch.result = result;
    var json = JSON.stringify(fullMatch);
    httpPut('/matches/' + matchid, json);
}


function printTournamentAHREF(tournament) {
    return '<a href=\"tournament.html?id=' + tournament._id + '\">' + tournament.name + '</a> (' + tournament.date  + ')';
}

function printCommitButton(tournament) {
    return '<button type=\"button\" onClick=\"commitTournament(\'' + tournament._id + '\');\">Commit tournament</button>';
}

function commitTournament(tournamentid) {
    httpPut("/tournaments/commit/" + tournamentid);
}

var getPlayersCallback = function() {
    if(this.readyState == this.DONE) {
        drawPlayers(JSON.parse(this.response));
    }
};

function drawPlayers(players) {
    for (var i = 0 ; i < players.length ; i++){
        var ul = document.getElementById('players');
        var li = document.createElement('li');
        li.appendChild(document.createTextNode(players[i].name + ' (' + players[i].elo.toFixed(0) + ')'));
        ul.appendChild(li);
    }
}