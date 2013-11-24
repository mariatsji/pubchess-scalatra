package net.groovygrevling

object PubchessLogic {

  def drawSingleTournament(players: List[Player]) : List[Match] = {
    require(players.count(_._id.isEmpty) == 0)
    val pairs: List[(Player, Player)] = naivePairs(players)
    val swapped: List[(Player, Player)] = swapEveryOther(pairs)
    val chopped: List[(Player, Player)] = chopFromBothEnds(swapped)
    chopped.map(
      (p: (Player, Player)) => Match(None, p._1._id.get, p._2._id.get)
    )
  }

  def drawDoubleTournament(players: List[Player]) : List[Match] =  {
    val single: List[Match] = drawSingleTournament(players)
    single ::: single.map(_.swapped)
  }

  private def naivePairs(players: List[Player]): List[(Player, Player)] =
    if(players.isEmpty)
      Nil
    else
      players.map(
        (p: Player) => (players.head, p)
      ).filter((t: (Player, Player)) => t._1._id.get != t._2._id.get) ::: naivePairs(players.tail)

  private def swapEveryOther(pairs: List[(Player, Player)]): List[(Player, Player)] = {
    val withIndex: List[((Player, Player), Int)] = pairs.zipWithIndex
    withIndex.map((t: ((Player, Player), Int)) => if (t._2 % 2 == 0) t._1 else t._1.swap)
  }

  private def chopFromBothEnds[A](pairs: List[A]): List[A] =
    if (pairs.isEmpty)
      Nil
    else
      pairs.head :: chopFromBothEnds(pairs.tail.reverse)


}
