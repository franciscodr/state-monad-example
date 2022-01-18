package io.example

import cats.MonadThrow
import cats.effect.kernel.Sync
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import io.example.algebras.{MessageProducer, SubscriptionPersistence, UserPersistence}
import io.example.model.User.SlackId
import io.example.model._

class SubscriptionService[F[_]: Sync](
    subscriptionPersistence: SubscriptionPersistence[F],
    userPersistence: UserPersistence[F],
    messageProducer: MessageProducer[F]
) {

  private[this] def getUserId(slackId: SlackId): F[User.Id] =
    userPersistence
      .getUserBySlackId(slackId)
      .map(_.id)
      .handleErrorWith { _ =>
        for {
          userId <- User.Id.generate
          _      <- userPersistence.addUser(User(userId, slackId)).as(userId)
        } yield userId
      }

  private[this] def sendSubscribeNotification(repository: Repository): F[Unit] =
    subscriptionPersistence
      .getSubscriptionsByRepository(repository)
      .flatMap(activeSubscriptions =>
        if (activeSubscriptions.length == 1)
          messageProducer.sendMessage(SubscriptionMessage(repository, SubscriptionAction.Subscribe))
        else
          Sync[F].unit
      )

  private[this] def sendUnsubscribeNotification(repository: Repository): F[Unit] =
    subscriptionPersistence
      .getSubscriptionsByRepository(repository)
      .flatMap(activeSubscriptions =>
        if (activeSubscriptions.isEmpty)
          messageProducer
            .sendMessage(SubscriptionMessage(repository, SubscriptionAction.Unsubscribe))
        else
          Sync[F].unit
      )

  def subscribeRepository(slackId: User.SlackId, repository: Repository): F[Unit] = for {
    userId <- getUserId(slackId)
    _      <- subscriptionPersistence.addSubscription(NewSubscription(userId, repository))
    _      <- sendSubscribeNotification(repository)
  } yield ()

  def unsubscribeRepository(slackId: User.SlackId, repository: Repository): F[Unit] = for {
    userId <- getUserId(slackId)
    _      <- subscriptionPersistence.removeSubscription(userId, repository)
    _      <- sendUnsubscribeNotification(repository)
  } yield ()
}
