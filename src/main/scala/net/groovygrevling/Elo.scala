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
}
