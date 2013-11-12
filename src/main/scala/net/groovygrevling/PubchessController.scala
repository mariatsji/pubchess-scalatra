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

  get("/players") {
    players.find().map(mongoToPlayer).toList
  }

  get("/players/:id") {
    val query = MongoDBObject("_id" -> new ObjectId(params("id")))
    players.findOne(query) map mongoToPlayer
  }

  post("/players") {
    parsedBody.extractOpt[Player].map { player =>
      val doc = jsToMongo(Extraction.decompose(player))
      players.insert(doc)
      mongoToPlayer(doc)
    } match {
      case None => BadRequest
      case Some(player) => Created(player)
    }
  }

  put("/players/:id") {
    parsedBody.extractOpt[Player].map { player =>
      val query = MongoDBObject("_id" -> new ObjectId(params("id")))
      val doc = playerToMongo(player)
      players.update(query, doc)
      mongoToPlayer(doc)
    } match {
      case None => BadRequest
      case Some(player) => Ok(player)
    }
  }

  delete("/players/:id") {
    val query = MongoDBObject("_id" -> new ObjectId(params("id")))
    players.remove(query)
  }

  def mongoToPlayer(obj: DBObject): Player = mongoToJs(obj).extract[Player]
  def playerToMongo(player: Player): DBObject = jsToMongo(Extraction.decompose(player))
  def jsToMongo(value: JValue): DBObject = JObjectParser.parse(value)
  def mongoToJs(obj: Any): JValue = JObjectParser.serialize(obj)

}
