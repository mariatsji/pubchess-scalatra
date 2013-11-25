function getPlayers() {
    var players = httpGet('/players');
    return JSON.parse(players);
}

function getTournaments() {
    var tournaments = httpGet('/tournaments');
    return JSON.parse(tournaments);
}

function httpGet(theUrl) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open('GET', theUrl, false);
    xmlHttp.send(null);
    return xmlHttp.responseText;
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

function httpPost(theUrl, json) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open('POST', theUrl, true);
    xmlHttp.setRequestHeader('Content-type', 'application/json; charset=UTF-8');
    xmlHttp.send(json);
    xmlHttp.onloadend = function () {
        console.log('completed sending new player')
    }
}

function getTournament(tournamentid) {
    var tournament = httpGet('/tournaments/' + tournamentid);
    return JSON.parse(tournament);
}

function getMatch(matchid) {
    var match = httpGet('/matches/' + matchid);
    return JSON.parse(match);
}

function getRequestParameter(name){
    if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
        return decodeURIComponent(name[1]);
}

function getPlayer(playerid) {
    var player = httpGet('/players/' + playerid);
    return JSON.parse(player);
}

function printMatchLI(match) {
    return '<li>' + match._id + ' (' + getPlayer(match.white_id).name + ') vs (' + getPlayer(match.black_id).name + ') </li>';
}

function printTournamentAHREF(tournament) {
    return '<a href=\"tournament.html?id=' + tournament._id + '\">' + tournament.name + '</a> (' + tournament.date + ')';
}