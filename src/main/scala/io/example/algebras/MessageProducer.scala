package io.example.algebras

import io.example.model.SubscriptionMessage

trait MessageProducer[F[_]] {
  def sendMessage(message: SubscriptionMessage): F[Unit]
}
