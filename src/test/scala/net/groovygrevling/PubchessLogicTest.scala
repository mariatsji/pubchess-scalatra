package net.groovygrevling

import org.scalatra.test.scalatest.ScalatraFlatSpec
import org.scalatest.matchers.ShouldMatchers

class PubchessLogicTest extends ScalatraFlatSpec with ShouldMatchers {

  "PubchessLogic" should "draw fair black and white balanced single tournament" in {
    val matches: List[Match] = PubchessLogic.drawSingleTournament(
      List(Player(Some("1"), "Sjur"), Player(Some("2"), "Vasja"), Player(Some("3"), "Pixie")))
    matches.size should be(3)
    matches(0).white_id should be ("1")
    matches(0).black_id should be ("2")
    matches(1).white_id should be ("2")
    matches(1).black_id should be ("3")
    matches(2).white_id should be ("3")
    matches(2).black_id should be ("1")
  }

}
