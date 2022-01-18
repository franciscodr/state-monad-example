package io.example.model

sealed abstract class SubscriptionAction extends Product with Serializable

object SubscriptionAction {
  case object Subscribe extends SubscriptionAction
  case object Unsubscribe extends SubscriptionAction
}

final case class SubscriptionMessage(repository: Repository, action: SubscriptionAction)
