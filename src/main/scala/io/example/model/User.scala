package io.example.model

import cats.effect.Sync
import io.example.model.User.{Id, SlackId}

import java.util.UUID

final case class User(id: Id, slackId: SlackId)

object User {
  final case class Id(value: UUID) extends AnyVal

  object Id {
    def generate[F[_]: Sync]: F[Id] = Sync[F].delay(Id(UUID.randomUUID()))
  }

  final case class SlackId(value: String) extends AnyVal
}
