package net.groovygrevling

import org.scalatra.test.scalatest.ScalatraFlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.json4s.jackson.Serialization._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory
import com.mongodb.casbah.Imports._

class PubchessControllerTest extends ScalatraFlatSpec with ShouldMatchers {

  val logger = LoggerFactory.getLogger(getClass)

  implicit val jsonFormats = DefaultFormats
  val mongoClient = MongoClient()
  val db = mongoClient("chess")
  val players = db("players-test")
  val matches = db("matches-test")

  addFilter(new PubchessController(players, matches), "/*")

  val jsonContentType = "Content-Type" -> "application/json"

  val newPlayer = Player(None, "Sjur")
  
  def createMatch(white: Player, black: Player) = {
    val whiteDB: Player = createPlayer(white)
    val blackDB: Player = createPlayer(black)
    val myMatch: Match = Match(None, whiteDB._id.get, blackDB._id.get)
    post("/matches", body = write(myMatch).getBytes, headers = Map(jsonContentType)) {
      status must be (201)
      val created = fromJson[Match](body)
      created
    }
  }
  val white = Player(None, "Vasja")
  val black = Player(None, "Pixie")

  def createPlayer(player: Player) =
    post("/players", body = write(player).getBytes, headers = Map(jsonContentType)) {
      status must be (201)
      val created = fromJson[Player](body)
      created
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
      status must be (200)
      fromJson[List[Player]](body).map(_.copy(_id = None)) must equal(List(newPlayer))
    }
  }

  it should "find single player" in {
    players.drop()
    val beforeUpdate: Player = createPlayer(newPlayer)


    get("/players/" + beforeUpdate._id.get) {
      status must be (200)
      fromJson[Player](body) must equal(beforeUpdate)
    }
  }
  
  it should "delete single player" in {
    players.drop()
    val player: Player = createPlayer(newPlayer)
    
    delete("/players/" + player._id.get) {
      status must be (200)

      get("/players/" + player._id.get) {
        status must be (200)
        body must equal("")
      }
    }
  }

  it should "update single player" in {
    players.drop()
    val player = createPlayer(Player(None, "Sjur"))
    val updatedPlayer = Player(player._id, "Sjuur")

    put("/players/" + player._id.get, body = write(updatedPlayer).getBytes, headers = Map(jsonContentType)) {
      status must be (200)

      get("/players/" + player._id.get) {
        status must be (200)

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
      status must be (200)
      fromJson[List[Match]](body) must equal(List(firstMatch, secondMatch))
    }
  }

  it should "find single match" in {
    matches.drop()
    val firstMatch: Match = createMatch(white, black)

    get("/matches/" + firstMatch._id.get) {
      status must be (200)
      fromJson[Match](body) must equal(firstMatch)
    }
  }

  it should "delete single match" in {
    matches.drop()
    val myMatch: Match = createMatch(white, black)

    delete("/matches/" + myMatch._id.get) {
      status must be (200)

      get("/matches/" + myMatch._id.get) {
        status must be (200)
        body must equal("")
      }
    }
  }

  it should "update single match" in {
    matches.drop()
    val myMatch: Match = createMatch(white,black)
    val updatedMatch: Match = myMatch.setResult(Result.BLACK_WON)

    put("/matches/" + myMatch._id.get, body = write(updatedMatch).getBytes, headers = Map(jsonContentType)) {
      status must be (200)

      get("/matches/" + myMatch._id.get) {
        status must be (200)

        val myMatchReturned: Match = fromJson[Match](body)
        myMatchReturned must equal(updatedMatch)

      }
    }
  }


}
