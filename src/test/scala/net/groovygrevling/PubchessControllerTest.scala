package net.groovygrevling

import org.scalatra.test.scalatest.ScalatraFlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.json4s.jackson.Serialization._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory
import com.mongodb.casbah.Imports._
import java.util.{Calendar, Date}

class PubchessControllerTest extends ScalatraFlatSpec with ShouldMatchers {

  implicit val jsonFormats = DefaultFormats
  val jsonContentType = "Content-Type" -> "application/json"

  val logger = LoggerFactory.getLogger(getClass)

  val mongoClient = MongoClient()
  val db = mongoClient("chess")
  val players = db("players-test")
  val matches = db("matches-test")
  val tournaments = db("tournaments-test")
  val elos = db("elos-test")

  val newPlayer = Player(None, "Sjur")

  val white = Player(None, "Vasja")
  val black = Player(None, "Pixie")

  private val pc: PubchessController = new PubchessController(players, matches, tournaments, elos)
  addFilter(pc, "/*")

  def createPlayer(player: Player) =
    post("/players", body = write(player).getBytes, headers = Map(jsonContentType)) {
      status must be(201)
      val created = fromJson[Player](body)
      created
    }

  def createMatch(white: Player, black: Player): Match = {
    val whiteDB: Player = createPlayer(white)
    val blackDB: Player = createPlayer(black)
    val myMatch: Match = Match(None, whiteDB._id.get, blackDB._id.get)
    createMatch(myMatch)
  }

  def createMatch(m: Match): Match = {
    post("/matches", body = write(m).getBytes, headers = Map(jsonContentType)) {
      status must be(201)
      val created = fromJson[Match](body)
      created
    }
  }

  def createDoubleTournament(t: Tournament): Tournament = {
    post("/tournaments/double", body = write(t).getBytes, headers = Map(jsonContentType)) {
      status must be (201)
      val created = fromJson[Tournament](body)
      created
    }
  }

  def createDoubleTournament(name: String, players: List[Player]): Tournament = {
    val playersWithId = players.distinct.map(createPlayer)
    val tournament = Tournament(None, name, Calendar.getInstance().getTime, playersWithId.map(_._id.get), List())
    createDoubleTournament(tournament)
  }

  def createSingleTournament(t: Tournament): Tournament = {
    post("/tournaments/single", body = write(t).getBytes, headers = Map(jsonContentType)) {
      status must be (201)
      val created = fromJson[Tournament](body)
      created
    }
  }

  def createSingleTournament(name: String, players: List[Player]): Tournament = {
    val playersWithId = players.distinct.map(createPlayer)
    val tournament = Tournament(None, name, Calendar.getInstance().getTime, playersWithId.map(_._id.get), List())
    createSingleTournament(tournament)
  }

  def fromJson[T: Manifest](body: String) = parse(body).extract[T]


  "PubchessController" should "store a new player" in {
    players.drop()
    createPlayer(newPlayer)
  }

  it should "list all players" in {
    players.drop()
    createPlayer(newPlayer)

    get("/players") {
      status must be(200)
      fromJson[List[Player]](body).map(_.copy(_id = None)) must equal(List(newPlayer))
    }
  }

  it should "find single player" in {
    players.drop()
    val beforeUpdate: Player = createPlayer(newPlayer)


    get("/players/" + beforeUpdate._id.get) {
      status must be(200)
      fromJson[Player](body) must equal(beforeUpdate)
    }
  }

  it should "delete single player" in {
    players.drop()
    val player: Player = createPlayer(newPlayer)

    delete("/players/" + player._id.get) {
      status must be(200)

      get("/players/" + player._id.get) {
        status must be(200)
        body must equal("")
      }
    }
  }

  it should "update single player" in {
    players.drop()
    val player = createPlayer(Player(None, "Sjur"))
    val updatedPlayer = Player(player._id, "Sjuur")

    put("/players/" + player._id.get, body = write(updatedPlayer).getBytes, headers = Map(jsonContentType)) {
      status must be(200)

      get("/players/" + player._id.get) {
        status must be(200)

        val player1: Player = fromJson[Player](body)
        player1 must equal(updatedPlayer)

      }
    }
  }

  it should "store a new match" in {
    matches.drop()
    createMatch(white, black)
  }

  it should "list all matches" in {
    matches.drop()
    val firstMatch: Match = createMatch(white, black)
    val secondMatch: Match = createMatch(black, white)
    get("/matches") {
      status must be(200)
      fromJson[List[Match]](body) must equal(List(firstMatch, secondMatch))
    }
  }

  it should "find single match" in {
    matches.drop()
    val firstMatch: Match = createMatch(white, black)

    get("/matches/" + firstMatch._id.get) {
      status must be(200)
      fromJson[Match](body) must equal(firstMatch)
    }
  }

  it should "delete single match" in {
    matches.drop()
    val myMatch: Match = createMatch(white, black)

    delete("/matches/" + myMatch._id.get) {
      status must be(200)

      get("/matches/" + myMatch._id.get) {
        status must be(200)
        body must equal("")
      }
    }
  }

  it should "update single match" in {
    matches.drop()
    val myMatch: Match = createMatch(white, black)
    val updatedMatch: Match = myMatch.setResult(Result.BLACK_WON)

    put("/matches/" + myMatch._id.get, body = write(updatedMatch).getBytes, headers = Map(jsonContentType)) {
      status must be(200)

      get("/matches/" + myMatch._id.get) {
        status must be(200)

        val myMatchReturned: Match = fromJson[Match](body)
        myMatchReturned must equal(updatedMatch)

      }
    }
  }

  it should "store a new double tournament" in {
    tournaments.drop()
    createDoubleTournament("TestTournament", List(Player(None, "Sjur"), Player(None, "Vasja"), Player(None, "Pixie")))
  }

  it should "store a new single tournament" in {
    tournaments.drop()
    createSingleTournament("TestTournament", List(Player(None, "Sjur"), Player(None, "Vasja"), Player(None, "Pixie")))
  }

  it should "list all tournaments " in {
    tournaments.drop()
    val t1 = createSingleTournament("TestTournament1", List(Player(None, "Sjur"), Player(None, "Vasja"), Player(None, "Pixie")))
    val t2 = createDoubleTournament("TestTournament2", List(Player(None, "Sjur"), Player(None, "Vasja"), Player(None, "Pixie")))

    get("/tournaments") {
      status must be (200)
      fromJson[List[Tournament]](body) must equal(List(t1, t2))
    }
  }

  it should "commit a tournament" in {
    dropdbs()

    val t1 = createSingleTournament("TestTournament1", List(Player(None, "Sjur"), Player(None, "Vasja"), Player(None, "Pixie")))

    val matchlist = t1.matchids.flatMap(pc.getMatchFromDB)
    
    val m1 = matchlist(0).setResult(Result.WHITE_WON)
    val m2 = matchlist(1).setResult(Result.BLACK_WON)
    val m3 = matchlist(2).setResult(Result.WHITE_WON)

    List(m1,m2,m3).foreach(pc.updateMatchInDB)

    put("/tournaments/commit/" + t1._id.get) {
      status must be (200)
      val playerslist = t1.playerids.flatMap(pc.getPlayerFromDB)
      playerslist.count(_.elo == 1200) must equal(0)
    }
  }


  private def dropdbs() {
    tournaments.drop()
    matches.drop()
    players.drop()
    elos.drop()
  }
}
