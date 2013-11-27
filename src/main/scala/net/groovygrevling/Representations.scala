package net.groovygrevling

import java.util.Date

case class Player(_id: Option[String], name: String, elo: Double = 1200L) {
  def setElo (newElo: Double) = Player(_id, name, newElo)
}
case class Match(_id: Option[String], white_id: String, black_id: String, result: Int = Result.UNPLAYED) {
  def setResult (result: Int) = Match(_id, white_id, black_id, result)
  def swapped : Match = Match(_id, black_id, white_id, result)
}
case class Tournament(_id: Option[String], name: String, date: Date = new Date(), playerids: List[String], matchids: List[String]) {
  def setMatches(newMatchIds: List[String]) = Tournament(_id, name, date, playerids, newMatchIds)
}
case class EloArchived(_id: Option[String], playerid: String, date: Date, elo: Double)

object Result {
  val DRAW = 3
  val UNPLAYED = 0
  val WHITE_WON = 1
  val BLACK_WON = 2
}
