function getPlayers(){
    var players = httpGet("/players");
    return JSON.parse(players);
}

function httpGet(theUrl) {
   var xmlHttp = new XMLHttpRequest();
   xmlHttp.open( "GET", theUrl, false );
   xmlHttp.send( null );
   return xmlHttp.responseText;
}

function addPlayer(name){
    var player = {}
    player ["name"] = name;
    player ["elo"] = "1200";
    var postjson = JSON.stringify(player);
    
    httpPost("/players",postjson)
}

function httpPost(theUrl, json){
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "POST", theUrl, true );
    xmlHttp.setRequestHeader('Content-type','application/json; charset=UTF-8');
    xmlHttp.send( json );
    xmlHttp.onloadend = function() {
        console.log("completed sending new player")
    }
}
