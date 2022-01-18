package io.example.algebras

import io.example.model.{NewSubscription, Repository, Subscription, User}

trait SubscriptionPersistence[F[_]] {
  def addSubscription(subscription: NewSubscription): F[Unit]
  def getSubscriptionsByRepository(repository: Repository): F[List[Subscription]]
  def getSubscriptionsByUserId(userId: User.Id): F[List[Subscription]]
  def removeSubscription(userId: User.Id, repository: Repository): F[Unit]
}
