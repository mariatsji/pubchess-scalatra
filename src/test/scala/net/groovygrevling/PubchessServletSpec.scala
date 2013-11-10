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
  val players = db("players")

  addFilter(new PubchessController(players), "/players/*")

  val jsonContentType = "Content-Type" -> "application/json"

  val newPlayer = Player(None, "Sjur")
  val newIdplayer = Player(Some("1"), "Pixie")

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
    val createPlayer1: Player = createPlayer(newPlayer)

    get("/players/" + createPlayer1._id.get) {
      status must be (200)
      fromJson[Player](body) must equal(createPlayer1)
    }
  }




}
