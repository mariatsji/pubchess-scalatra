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
