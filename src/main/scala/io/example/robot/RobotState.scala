package io.example.robot

final case class RobotState(initialPosition: Position, moves: List[MoveDirection]) {
  def calculateCurrentPosition: Position =
    moves.foldLeft(initialPosition)((position, direction) =>
      direction match {
        case MoveDirection.Down  => position.moveDown
        case MoveDirection.Left  => position.moveLeft
        case MoveDirection.Right => position.moveRight
        case MoveDirection.Up    => position.moveUp
      }
    )
}

object RobotState {
  val empty: RobotState = RobotState(Position.initial, List.empty)
}
