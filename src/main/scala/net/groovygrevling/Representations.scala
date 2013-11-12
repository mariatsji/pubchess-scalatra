package net.groovygrevling

import java.util.Date

case class Player(_id: Option[String], name: String, elo: Double = 1200L)
case class Match(_id: Option[String], white: Option[String], black: Option[String], result: Int = Result.UNPLAYED)
case class Tournament(_id: Option[String], name: String, date: Option[Date], matches: List[Match])

object Result {
  val DRAW = 3
  val UNPLAYED = 0
  val WHITE_WON = 1
  val BLACK_WON = 2
}
