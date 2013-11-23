package net.groovygrevling

import java.util.Date

case class Player(_id: Option[String], name: String, elo: Double = 1200L)
case class Match(_id: Option[String], white_id: String, black_id: String, result: Int = Result.UNPLAYED) {
  def setResult (result: Int) : Match = {
    Match(_id, white_id, black_id, result)
  }
}
case class Tournament(_id: Option[String], name: String, date: Date = new Date(), matchids: List[String])

object Result {
  val DRAW = 3
  val UNPLAYED = 0
  val WHITE_WON = 1
  val BLACK_WON = 2
}
