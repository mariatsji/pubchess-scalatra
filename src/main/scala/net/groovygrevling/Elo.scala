package net.groovygrevling

object Elo {

  def calculate(whiteElo: Double, blackElo: Double, outcome: Int, kFactorWhite: Long, kFactorBlack: Long) = {
    val qWhite = getQValue(whiteElo)
    val qBlack = getQValue(blackElo)

    val eWhite = qWhite / (qWhite + qBlack)
    val eBlack = qBlack / (qWhite + qBlack)

    val newEloWhite = whiteElo + (kFactorWhite * (getSValue(outcome)._1 - eWhite))
    val newEloBlack = blackElo + (kFactorBlack * (getSValue(outcome)._2 - eBlack))

    (newEloWhite,newEloBlack)
  }

  private def getQValue(elo: Double) = math.pow(10, elo / 400)

  private def getSValue(outcome: Int): (Float,Float) =
    if (outcome == Result.WHITE_WON)
      (1f,0f)
    else if (outcome == Result.BLACK_WON)
      (0f,1f)
    else
      (0.5f, 0.5f)

  /**
   * FIDE rules:
   * K = 30 (was 25) for a player new to the rating list until s/he has completed events with a total of at least 30 games.[15]
   * K = 15 as long as a player's rating remains under 2400.
   * K = 10 once a player's published rating has reached 2400, and s/he has also completed events with a total of at least 30 games. Thereafter it remains permanently at 10.
   */
  def getKfactor(matches: List[Match], player: Player): Long = {
    def playedMatches: Int = matches.size
    if (playedMatches < 30) 30
    else {
      if(player.elo < 2400) 15 else 10
    }
  }
}
