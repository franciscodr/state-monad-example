package io.example.model

sealed abstract class ServiceError extends Throwable

final case class UserNotFound(slackId: User.SlackId) extends ServiceError
