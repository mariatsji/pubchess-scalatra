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
import java.io.File
import org.scalatra.json.JacksonJsonSupport

class PubchessController(playersDB: MongoCollection, matchesDB: MongoCollection) extends ScalatraFilter with JacksonJsonSupport {

  val logger = LoggerFactory.getLogger(getClass)
  protected implicit val jsonFormats: Formats = DefaultFormats

  get("/players") {
    logger.debug("finding all players")
    contentType = formats("json")
    playersDB.find().map(mongoToPlayer).toList
  }

  get("/players/:id") {
    contentType = formats("json")
    val id = params("id")
    logger.debug(s"finding player $id")
    val query = MongoDBObject("_id" -> new ObjectId(id))
    playersDB.findOne(query) map mongoToPlayer
  }

  post("/players") {
    contentType = formats("json")
    logger.debug("creating new player")
    parsedBody.extractOpt[Player].map { player =>
      val doc = jsToMongo(Extraction.decompose(player))
      playersDB.insert(doc)
      mongoToPlayer(doc)
    } match {
      case None => BadRequest
      case Some(player) => Created(player)
    }
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
    parsedBody.extractOpt[Match].map { myMatch =>
      val doc: DBObject = jsToMongo(Extraction.decompose(myMatch))
      matchesDB.insert(doc)
      mongoToMatch(doc)
    } match {
      case None => BadRequest
      case Some(myMatch) => Created(myMatch)
    }
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

  def mongoToPlayer(obj: DBObject): Player = mongoToJs(obj).extract[Player]
  def mongoToMatch(obj: DBObject): Match = mongoToJs(obj).extract[Match]
  def playerToMongo(player: Player): DBObject = jsToMongo(Extraction.decompose(player))
  def matchToMongo(myMatch: Match): DBObject = jsToMongo(Extraction.decompose(myMatch))
  def jsToMongo(value: JValue): DBObject = JObjectParser.parse(value)
  def mongoToJs(obj: Any): JValue = JObjectParser.serialize(obj)

}
