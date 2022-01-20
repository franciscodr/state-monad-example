package io.example.robot

import cats.data.State

trait CoolRobotService[F[_]] {
  def getCurrentPosition: F[Position]
  def moveRobot(moveDirection: MoveDirection): F[Unit]
}

class CoolRobotServiceImpl extends CoolRobotService[State[RobotState, *]] {
  override def getCurrentPosition: State[RobotState, Position] =
    State.inspect(_.calculateCurrentPosition)

  override def moveRobot(moveDirection: MoveDirection): State[RobotState, Unit] =
    State.modify(state => state.copy(moves = state.moves :+ moveDirection))

  /**
   * Alias for moveRobot(MoveDirection.Down)
   */
  def moveRobotDown: State[RobotState, Unit] = moveRobot(MoveDirection.Down)

  /**
   * Alias for moveRobot(MoveDirection.Left)
   */
  def moveRobotLeft: State[RobotState, Unit] = moveRobot(MoveDirection.Left)

  /**
   * Alias for moveRobot(MoveDirection.Right)
   */
  def moveRobotRight: State[RobotState, Unit] = moveRobot(MoveDirection.Right)

  /**
   * Alias for moveRobot(MoveDirection.Up)
   */
  def moveRobotUp: State[RobotState, Unit] = moveRobot(MoveDirection.Up)
}
