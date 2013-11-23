function getPlayers(){
    var players = httpGet("http://localhost:7002/players");
    return JSON.parse(players);
}

function httpGet(theUrl)
{
   var xmlHttp = null;

   xmlHttp = new XMLHttpRequest();
   xmlHttp.open( "GET", theUrl, false );
   xmlHttp.send( null );
   return xmlHttp.responseText;
}

function addPlayer(name){
    var player = {}
    player ["name"] = name;
    player ["elo"] = "1200";
    var postjson = JSON.stringify(player);
    
    httpPost("http://localhost:7002/players",postjson)
}

function httpPost(theUrl, json){
    var xmlHttp = null;
    
    xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "POST", theUrl, false );
    xmlHttp.send( json ); 
}
