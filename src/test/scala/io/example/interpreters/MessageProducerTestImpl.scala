package io.example.interpreters

import cats.data.StateT
import cats.effect.IO
import io.example.algebras.MessageProducer
import io.example.model.SubscriptionMessage

class MessageProducerTestImpl extends MessageProducer[StateT[IO, ServiceState, *]] {
  override def sendMessage(message: SubscriptionMessage): StateT[IO, ServiceState, Unit] = StateT {
    serviceState =>
      IO(serviceState.addSubscriptionMessage(message), ())
  }
}
