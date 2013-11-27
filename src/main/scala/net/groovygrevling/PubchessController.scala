package net.groovygrevling

import org.scalatra._
import com.mongodb.casbah.MongoCollection
import org.slf4j.LoggerFactory
import com.mongodb.DBObject
import org.json4s.JsonAST.JValue
import org.json4s.mongo.JObjectParser
import org.json4s.{DefaultFormats, Formats, Extraction}
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import org.scalatra.json.JacksonJsonSupport
import java.util.Date

class PubchessController(playersDB: MongoCollection, matchesDB: MongoCollection, tournamentsDB: MongoCollection, elosDB: MongoCollection) extends ScalatraFilter with JacksonJsonSupport {

  val logger = LoggerFactory.getLogger(getClass)
  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/players") {
    logger.debug("finding all players")
    contentType = formats("json")
    playersDB.find().map(mongoToPlayer).toList
  }

  def getPlayerFromDB(id: String) : Option[Player] = {
    val query = MongoDBObject("_id" -> new ObjectId(id))
    playersDB.findOne(query) map mongoToPlayer
  }

  get("/players/:id") {
    contentType = formats("json")
    val id = params("id")
    logger.debug(s"finding player $id")
    getPlayerFromDB(id)
  }

  post("/players") {
    contentType = formats("json")
    logger.debug("creating new player")
    parsedBody.extractOpt[Player].map { player =>
      val stored = storePlayerInDB(player)
      archiveNewElo(stored, stored.elo)
      stored
    } match {
      case None => BadRequest
      case Some(player) => Created(player)
    }
  }

  def storePlayerInDB(p: Player) = {
    val doc: DBObject = jsToMongo(Extraction.decompose(p))
    playersDB.insert(doc)
    mongoToPlayer(doc)
  }


  put("/players/:id") {
    contentType = formats("json")
    val id = params("id")
    logger.debug(s"changing player $id")
    parsedBody.extractOpt[Player].map { player =>
      val query = MongoDBObject("_id" -> new ObjectId(id))
      val doc = playerToMongo(player)
      playersDB.update(query, doc)
      mongoToPlayer(doc)
    } match {
      case None => BadRequest
      case Some(player) => Ok(player)
    }
  }

  delete("/players/:id") {
    contentType = formats("json")
    val id = params("id")
    logger.debug(s"deleting player $id")
    val query = MongoDBObject("_id" -> new ObjectId(id))
    playersDB.remove(query)
  }

  get("/matches") {
    contentType = formats("json")
    logger.debug("finding all matches")
    matchesDB.find().map(mongoToMatch).toList
  }

  get("/matches/:id") {
    contentType = formats("json")
    val id = params("id")
    matchesDB.findOne(MongoDBObject("_id" -> new ObjectId(id))).map(mongoToMatch)
  }

  post("/matches") {
    contentType = formats("json")
    logger.debug("creating new match")
    parsedBody.extractOpt[Match].map(storeMatchInDB) match {
      case None => BadRequest
      case Some(myMatch) => Created(myMatch)
    }
  }

  def storeMatchInDB(m: Match) = {
    val doc: DBObject = jsToMongo(Extraction.decompose(m))
    matchesDB.insert(doc)
    mongoToMatch(doc)
  }

  def updateMatchInDB(m: Match) = {
    val query = MongoDBObject("_id" -> new ObjectId(m._id.get))
    val doc = matchToMongo(m)
    matchesDB.update(query, doc)
    mongoToMatch(doc)
  }

  def updatePlayerInDB(p: Player) = {
    val query = MongoDBObject("_id" -> new ObjectId(p._id.get))
    val doc = playerToMongo(p)
    playersDB.update(query, doc)
    mongoToPlayer(doc)
  }
  
  def getMatchFromDB(id: String) = {
    val query = MongoDBObject("_id" -> new ObjectId(id))
    matchesDB.findOne(query) map mongoToMatch 
  }

  delete("/matches/:id") {
    contentType = formats("json")
    val id = params("id")
    logger.debug(s"deleting match $id")
    val query = MongoDBObject("_id" -> new ObjectId(id))
    matchesDB.remove(query)
  }

  put("/matches/:id") {
    contentType = formats("json")
    val id = params("id")
    logger.debug(s"changing match $id")
    parsedBody.extractOpt[Match].map { myMatch =>
      val query = MongoDBObject("_id" -> new ObjectId(id))
      val doc = matchToMongo(myMatch)
      matchesDB.update(query, doc)
      val returned = mongoToMatch(doc)
      returned
    } match {
      case None=> BadRequest
      case Some(m) => Ok(m)
    }
  }

  get("/tournaments") {
    contentType = formats("json")
    logger.debug("showing all tournaments")
    tournamentsDB.find().map(mongoToTournament).toList
  }

  get("/tournaments/:id") {
    contentType = formats("json")
    val id = params("id")
    logger.debug(s"finding tournament $id")
    getTournamentFromDB(id)
  }

  def getTournamentFromDB(id: String) : Option[Tournament] = {
    val query = MongoDBObject("_id" -> new ObjectId(id))
    tournamentsDB.findOne(query) map mongoToTournament
  }

  post("/tournaments/double") {
    contentType = formats("json")
    logger.debug("creating double tournament")
    parsedBody.extractOpt[Tournament].map { tournament =>
      val players = tournament.playerids.flatMap(getPlayerFromDB)
      val matches: List[Match] = PubchessLogic.drawDoubleTournament(players)
      val matchesWithId: List[Match] = matches.map(storeMatchInDB)
      val newTournament: Tournament = tournament.setMatches(matchesWithId.map(_._id.get))
      val doc: DBObject = jsToMongo(Extraction.decompose(newTournament))
      tournamentsDB.insert(doc)
      mongoToTournament(doc)
    } match {
      case None => BadRequest
      case Some(t) => Created(t)
    }
  }

  post("/tournaments/single") {
    contentType = formats("json")
    logger.debug("creating single tournament")
    parsedBody.extractOpt[Tournament].map { tournament =>
      val players = tournament.playerids.flatMap(getPlayerFromDB)
      val matches: List[Match] = PubchessLogic.drawSingleTournament(players)
      val matchesWithId: List[Match] = matches.map(storeMatchInDB)
      val newTournament: Tournament = tournament.setMatches(matchesWithId.map(_._id.get))
      val doc: DBObject = jsToMongo(Extraction.decompose(newTournament))
      tournamentsDB.insert(doc)
      mongoToTournament(doc)
    } match {
      case None => BadRequest
      case Some(t) => Created(t)
    }
  }

  def archiveNewElo(player: Player, elo: Double) = {
    val eloarch = EloArchived(None, player._id.get, new Date(), elo)
    val doc = eloToMongo(eloarch)
    elosDB.insert(doc)
    mongoToElo(doc)
  }
  

  put("/tournaments/commit/:id") {
    contentType = formats("json")
    val id = params("id")
    logger.debug(s"commiting tournament $id")
    getTournamentFromDB(id).map { tournament =>
      val matches = tournament.matchids.flatMap(getMatchFromDB)
      if(matches.exists(_.result == Result.UNPLAYED)) {
        None
      } else {
        matches.map((m : Match) =>
          {
            val white: Player = getPlayerFromDB(m.white_id).get
            val black: Player = getPlayerFromDB(m.black_id).get
            val kFactorWhite: Long = getKfactor(m.white_id)
            val kFactorBlack: Long = getKfactor(m.black_id)
            val newElos: (Double, Double) = Elo.calculate(white.elo, black.elo, m.result, kFactorWhite, kFactorBlack)
            val newEloWhite = newElos._1
            val newEloBlack = newElos._2
            val newWhite = white.setElo(newEloWhite)
            archiveNewElo(newWhite, newEloWhite)
            val newBlack = black.setElo(newEloBlack)
            archiveNewElo(newBlack, newEloBlack)
            updatePlayerInDB(newWhite)
            updatePlayerInDB(newBlack)
            tournament
          }
        )
      }
    } match {
      case None=> BadRequest
      case Some(t) => Ok(t)
    }
  }

  def allEverMatchesForPlayer(playerid: String) = {
    val allMatches = matchesDB.find().map(mongoToMatch).toList
    allMatches.filter((m: Match) => m.white_id == playerid || m.black_id == playerid)
  }


  /**
   * FIDE rules:
   * K = 30 (was 25) for a player new to the rating list until s/he has completed events with a total of at least 30 games.[15]
   * K = 15 as long as a player's rating remains under 2400.
   * K = 10 once a player's published rating has reached 2400, and s/he has also completed events with a total of at least 30 games. Thereafter it remains permanently at 10.
   */
  def getKfactor(playerid: String): Long = {
    def playedMatches: Int = allEverMatchesForPlayer(playerid).size
    if (playedMatches < 30) 30
    else {
      if(getPlayerFromDB(playerid).get.elo < 2400) 15 else 10
    }
  }

  def mongoToPlayer(obj: DBObject): Player = mongoToJs(obj).extract[Player]
  def mongoToMatch(obj: DBObject): Match = mongoToJs(obj).extract[Match]
  def mongoToTournament(obj: DBObject): Tournament = mongoToJs(obj).extract[Tournament]
  def mongoToElo(obj: DBObject): EloArchived = mongoToJs(obj).extract[EloArchived]
  def playerToMongo(player: Player): DBObject = jsToMongo(Extraction.decompose(player))
  def matchToMongo(myMatch: Match): DBObject = jsToMongo(Extraction.decompose(myMatch))
  def tournamentToMongo(myTournament: Tournament): DBObject = jsToMongo(Extraction.decompose(myTournament))
  def eloToMongo(elo: EloArchived): DBObject = jsToMongo(Extraction.decompose(elo))
  def jsToMongo(value: JValue): DBObject = JObjectParser.parse(value)
  def mongoToJs(obj: Any): JValue = JObjectParser.serialize(obj)

}
