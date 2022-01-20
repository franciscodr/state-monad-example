package io.example.robot

import cats.Show

sealed abstract class MoveDirection extends Product with Serializable

object MoveDirection {
  case object Down extends MoveDirection

  case object Left extends MoveDirection

  case object Right extends MoveDirection

  case object Up extends MoveDirection

  implicit val showInstance: Show[MoveDirection] = new Show[MoveDirection] {
    override def show(direction: MoveDirection): String = direction match {
      case Down  => "down"
      case Left  => "left"
      case Right => "right"
      case Up    => "up"
    }
  }
}
