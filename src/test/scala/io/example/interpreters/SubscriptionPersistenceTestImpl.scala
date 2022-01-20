package io.example.interpreters

import cats.data.StateT
import cats.effect.IO
import io.example.algebras.SubscriptionPersistence
import io.example.model.{NewSubscription, Repository, Subscription, User}

class SubscriptionPersistenceTestImpl extends SubscriptionPersistence[StateT[IO, ServiceState, *]] {
  override def addSubscription(subscription: NewSubscription): StateT[IO, ServiceState, Unit] =
    StateT.modifyF { serviceState =>
      Subscription.Id
        .generate[IO]
        .map(id => serviceState.addSubscription(id, subscription.userId, subscription.repository))
    }

  override def getSubscriptionsByRepository(
      repository: Repository
  ): StateT[IO, ServiceState, List[Subscription]] = StateT.inspect { serviceState =>
    serviceState.subscriptionDbTable.filter(s => s.repository == repository && s.deletedAt.isEmpty)
  }

  override def getSubscriptionsByUserId(
      userId: User.Id
  ): StateT[IO, ServiceState, List[Subscription]] =
    StateT.inspect { serviceState =>
      serviceState.subscriptionDbTable.filter(_.user == userId)
    }

  override def removeSubscription(
      userId: User.Id,
      repository: Repository
  ): StateT[IO, ServiceState, Unit] =
    StateT.modify { serviceState =>
      serviceState.cancelSubscription(userId, repository)
    }
}
