package io.example.robot

import cats.Show

final case class Position(x: Long, y: Long) {
  def moveDown: Position  = this.copy(y = this.y - 1)
  def moveLeft: Position  = this.copy(x = this.x - 1)
  def moveRight: Position = this.copy(x = this.x + 1)
  def moveUp: Position    = this.copy(y = this.y + 1)
}

object Position {
  val initial: Position = Position(0, 0)

  implicit val showInstance: Show[Position] = new Show[Position] {
    override def show(position: Position): String = s"(${position.x},${position.y})"
  }
}
