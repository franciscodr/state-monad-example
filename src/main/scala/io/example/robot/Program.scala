package io.example.robot

import cats.data.State
import cats.syntax.flatMap._
import cats.syntax.show._

object Program extends App {

  val coolRobotService = new CoolRobotServiceImpl

  import coolRobotService._

  def buildRobotItinerary: (RobotState, Position) => String = (robotState, currentPosition) => {
    val itinerary =
      if (robotState.moves.isEmpty) "I ran out of battery"
      else s"I went ${robotState.moves.map(direction => direction.show).mkString(", ")}"

    show"""
            |I started my walk at position ${robotState.initialPosition}
            |Then $itinerary
            |So, I ended up at position $currentPosition
    """.stripMargin
  }

  val walk: State[RobotState, Position] = moveRobotUp >>
    moveRobotUp >>
    moveRobotRight >>
    moveRobotDown >>
    moveRobotRight >>
    moveRobotLeft >>
    moveRobotDown >>
    getCurrentPosition

  println(buildRobotItinerary.tupled(walk.run(RobotState.empty).value))
}
