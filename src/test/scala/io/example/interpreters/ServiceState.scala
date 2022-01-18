package io.example.interpreters

import io.example.model.{Repository, Subscription, SubscriptionMessage, User}

import java.time.LocalDateTime
import java.util.UUID

case class ServiceState(
    userDbTable: List[User],
    subscriptionDbTable: List[Subscription],
    subscriptionKafkaTopic: List[SubscriptionMessage]
) {
  def addSubscription(id: Subscription.Id, userId: User.Id, repository: Repository): ServiceState =
    this.copy(subscriptionDbTable =
      this.subscriptionDbTable :+ Subscription(id, userId, repository, None)
    )

  def addSubscriptionMessage(message: SubscriptionMessage): ServiceState =
    this.copy(subscriptionKafkaTopic = this.subscriptionKafkaTopic :+ message)

  def addUser(user: User): ServiceState = this.copy(userDbTable = this.userDbTable :+ user)

  def cancelSubscription(userId: User.Id, repository: Repository): ServiceState =
    this.copy(subscriptionDbTable =
      this.subscriptionDbTable.map(subscription =>
        if (subscription.user == userId && subscription.repository == repository)
          subscription.copy(deletedAt = Some(LocalDateTime.now()))
        else
          subscription
      )
    )
}

object ServiceState {
  val empty: ServiceState = ServiceState(List.empty, List.empty, List.empty)
  def generateSubscriptions(
      repository: Repository,
      usersSlackId: List[User.SlackId]
  ): ServiceState = {
    val users = usersSlackId.map(slackId => User(User.Id(UUID.randomUUID()), slackId))
    ServiceState(
      users,
      users.map(user =>
        Subscription(Subscription.Id(UUID.randomUUID()), user.id, repository, None)
      ),
      List.empty
    )
  }
}
