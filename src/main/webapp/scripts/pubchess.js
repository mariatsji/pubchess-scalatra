function getRequestParameter(name) {
    var retString = 'not found';
    if (name = (new RegExp('[?&]' + encodeURIComponent(name) + '=([^&]*)')).exec(location.search)) {
        retString = decodeURIComponent(name[1]);
    }
    return retString;

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
    xmlHttp.open('POST', theUrl, false);
    xmlHttp.setRequestHeader('Content-type', 'application/json; charset=UTF-8');
    xmlHttp.send(json);
}

function asyncPost(theUrl, json, callbackFunction) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = callbackFunction;
    xmlHttp.open('POST', theUrl, true);
    xmlHttp.setRequestHeader('Content-type', 'application/json; charset=UTF-8');
    xmlHttp.send(json);
}

function httpPut(theUrl, json) {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open('PUT', theUrl, false);
    xmlHttp.setRequestHeader('Content-type', 'application/json; charset=UTF-8');
    xmlHttp.send(json);
}

var result_white_won = 1;
var result_black_won = 2;
var result_draw = 3;

function getPlayer(playerid) {
    var player = httpGet('/players/' + playerid);
    return JSON.parse(player);
}

function getPlayers(callbackFunc) {
    asyncGet('/players', callbackFunc);
}

function getPlayersSync() {
    var players = httpGet('/players');
    return JSON.parse(players);
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

function addPlayer(name) {
    var player = {};
    player ['name'] = name;
    player ['elo'] = '1200';
    var json = JSON.stringify(player);
    httpPost('/players', json);
    drawPlayers(getPlayersSync());
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
    printTournaments();
}

function addTournamentDouble(name, participants) {
    var tournament = createTournamentObject(name, participants);
    var json = JSON.stringify(tournament);
    httpPost('/tournaments/double', json);
    printTournaments();
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
    location.reload(true);
}

function printTournamentAHREF(tournament) {
    return '<a href=\"tournament.html?id=' + tournament._id + '\">' + tournament.name + '</a> (' + tournament.date  + ')';
}

function printTournamentresultAHREF(tournament) {
    return '<a href=\"tournamentresult.html?id=' + tournament._id + '\">' + tournament.name + '</a>';
}

function printCommitButton(tournament) {
    return '<button type=\"button\" onClick=\"commitTournament(\'' + tournament._id + '\');\">Commit tournament</button>';
}

function commitTournament(tournamentid) {
    httpPut("/tournaments/commit/" + tournamentid);
}

var drawPlayersLICallback = function() {
    if(this.readyState == this.DONE) {
        drawPlayers(JSON.parse(this.response));
    }
};

function drawPlayersSelectable(players) {
    for (var i = 0; i < players.length; i++) {
        drawPlayerSelectable(players[i]);
    }
}

var drawPlayersSelectableCallback = function() {
    if(this.readyState == this.DONE) {
        drawPlayersSelectable(JSON.parse(this.response));
    }
};

function drawPlayers(players) {
    var ul = document.getElementById('players');
    ul.innerHTML = '';
    for (var i = 0 ; i < players.length ; i++){
        var li = document.createElement('li');
        var a = document.createElement('a');
        a.appendChild(document.createTextNode(players[i].name + ' (' + players[i].elo.toFixed(0) + ')'));
        a.setAttribute('href', 'player.html?id=' + players[i]._id);
        li.appendChild(a);
        ul.appendChild(li);
    }
}

function drawPlayersSelectable(players) {
    var ul = document.getElementById('players');
    ul.innerHTML = '';
    for (var i = 0 ; i < players.length ; i++){
        var player = players[i];
        var li = document.createElement('li');
        var input = document.createElement('input');
        input.setAttribute('type', 'checkbox');
        input.setAttribute('name', 'participants');
        input.setAttribute('value', player._id);
        li.appendChild(input);
        li.appendChild(document.createTextNode(player.name));
        ul.appendChild(li);
    }
}

function drawResults(tournamentid) {
    var ol = document.getElementById('result');
    ol.innerHTML = '';
    var tournament = getTournament(tournamentid);
    var matches = new Array();
    for (var i = 0 ; i < tournament.matchids.length ; i ++) {
        matches[i] = getMatch(tournament.matchids[i]);
    }
    var players = new Array();
    var results = new Array();
    for (var i = 0 ; i < tournament.playerids.length; i ++) {
        players[i] = getPlayer(tournament.playerids[i]);
        results[i] = calculatePoints(players[i], matches);
    }
    results.sort(sortResults);
    for (var i = 0 ; i < results.length; i ++) {
        var li = document.createElement('li');
        li.appendChild(document.createTextNode(printResult(results[i])));
        ol.appendChild(li);
    }
}

function sortResults(resulta, resultb) {
    if (resulta.points == resultb.points) {
        return resultb.blackwins - resulta.blackwins;
    }
    return resultb.points - resulta.points;
}


function calculatePoints(player, matches) {
    var points = 0;
    var whitewins = 0;
    var blackwins = 0;
    var draws = 0;
    for (var i = 0 ; i < matches.length; i ++) {
        var match = matches[i];
        if(match.result == result_draw) {
            points = points + 0.5;
            draws = draws + 1;
        } else if ((match.result == result_white_won) && (match.white_id == player._id)) {
            points = points + 1;
            whitewins = whitewins + 1;
        } else if ((match.result == result_black_won) && (match.black_id == player._id)) {
            points = points + 1;
            blackwins = blackwins + 1;
        }
    }
    return createResultObject(player, points, whitewins, blackwins, draws);
}

function createResultObject(player, points, whitewins, blackwins, draws) {
    var result = new Object();
    result.player = player;
    result.points = points;
    result.whitewins = whitewins;
    result.blackwins = blackwins;
    result.draws = draws;
    return result;
}

function printResult(result) {
    var txt = result.player.name
    + ' (' + result.player.elo.toFixed(0) + ') '
    + result.points + ' (' + result.whitewins + ' wins with white, '
    + result.blackwins + ' with black, '
    + result.draws + ' remis)';
    return txt;
}

function printTournaments() {
    var ul = document.getElementById('tournaments');
    ul.innerHTML = '';
    var tournaments = getTournaments();
    for(var i = 0; i < tournaments.length; i++) {
        ul.innerHTML = ul.innerHTML + printTournamentAHREF(tournaments[i]) + '<br />';
    }
}

function showPlayerDetails(playerid) {
    var player = getPlayer(playerid);
    var div = document.getElementById('player');
    var text = '' + player.name + ' : ' + player._id + ' (' + player.elo.toFixed(0) + ')';
    div.appendChild(document.createTextNode(text));
}