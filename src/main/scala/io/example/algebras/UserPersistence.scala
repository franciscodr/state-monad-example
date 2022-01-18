package io.example.algebras

import io.example.model.User

trait UserPersistence[F[_]] {
  def addUser(user: User): F[Unit]
  def getUserBySlackId(slackId: User.SlackId): F[User]
}
