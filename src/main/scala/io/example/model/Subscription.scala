package io.example.model

import cats.effect.Sync
import io.example.model.Subscription.Id

import java.time.LocalDateTime
import java.util.UUID

final case class Subscription(
    id: Id,
    user: User.Id,
    repository: Repository,
    deletedAt: Option[LocalDateTime]
)

object Subscription {
  final case class Id(value: UUID) extends AnyVal

  object Id {
    def generate[F[_]: Sync]: F[Id] = Sync[F].delay(Id(UUID.randomUUID()))
  }
}
