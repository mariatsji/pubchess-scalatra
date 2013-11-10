package net.groovygrevling

import org.scalatra._
import com.mongodb.casbah.MongoCollection
import org.slf4j.LoggerFactory
import com.mongodb.DBObject
import org.json4s.JsonAST.JValue
import org.json4s.mongo.JObjectParser
import org.json4s.Extraction
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject

class PubchessController(players: MongoCollection) extends PubchessStack {

  val logger = LoggerFactory.getLogger(getClass)

  get("/") {
    <html>
      <body>
        <h1>Pubchess</h1>
        hi!!11
      </body>
    </html>
  }

  get("/players") {
    players.find().map(toPlayer).toList
  }

  get("/players/:id") {
    val query = MongoDBObject("_id" -> new ObjectId(params("id")))
    players.findOne(query) map toPlayer
  }

  post("/players") {
    parsedBody.extractOpt[Player].map { player =>
      val doc = jsToMongo(Extraction.decompose(player))
      players.insert(doc)
      toPlayer(doc)
    } match {
      case None => BadRequest
      case Some(player) => Created(player)
    }
  }

  def toPlayer(obj: DBObject) = mongoToJs(obj).extract[Player]
  def jsToMongo(value: JValue): DBObject = JObjectParser.parse(value)
  def mongoToJs(obj: Any): JValue = JObjectParser.serialize(obj)

}
