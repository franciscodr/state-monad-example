package io.example

import cats.data.StateT
import cats.effect.IO
import io.example.interpreters._
import io.example.model.{Repository, SubscriptionAction, User}

class SubscriptionServiceTests extends munit.CatsEffectSuite {
  val subscriptionService = new SubscriptionService[StateT[IO, ServiceState, *]](
    new SubscriptionPersistenceTestImpl(),
    new UserPersistenceTestImpl(),
    new MessageProducerTestImpl()
  )

  test("The service only sends a message to the queue for the first subscription to a repo") {
    val slackIdForUserA = User.SlackId("abc")
    val slackIdForUserB = User.SlackId("def")
    val repository      = Repository(Repository.Organization("47deg"), Repository.Name("test"))

    val subscriptionForUserA = subscriptionService.subscribeRepository(slackIdForUserA, repository)
    val subscriptionForUserB = subscriptionService.subscribeRepository(slackIdForUserB, repository)

    val serviceStates = for {
      stateAfterSubscriptionForUserA <- subscriptionForUserA.runS(ServiceState.empty)
      stateAfterSubscriptionForUserB <- subscriptionForUserB.runS(stateAfterSubscriptionForUserA)
    } yield (stateAfterSubscriptionForUserA, stateAfterSubscriptionForUserB)

    serviceStates.map { case (stateAfterSubscriptionForUserA, stateAfterSubscriptionForUserB) =>
      stateAfterSubscriptionForUserA.subscriptionKafkaTopic.length == 1 &&
      stateAfterSubscriptionForUserA.subscriptionKafkaTopic.exists(message =>
        message.repository == repository && message.action == SubscriptionAction.Subscribe
      ) &&
      stateAfterSubscriptionForUserB.subscriptionKafkaTopic.length == 1
    }.assert
  }

  test(
    "After canceling a subscription, if there are no active subscriptions for the repo, the service sends one message to the queue."
  ) {
    val slackIdForUserA = User.SlackId("abc")
    val slackIdForUserB = User.SlackId("def")
    val repository      = Repository(Repository.Organization("47deg"), Repository.Name("test"))

    val subscriptionCancellationForUserA =
      subscriptionService.unsubscribeRepository(slackIdForUserA, repository)
    val subscriptionCancellationForUserB =
      subscriptionService.unsubscribeRepository(slackIdForUserB, repository)

    val initialState =
      ServiceState.generateSubscriptions(repository, List(slackIdForUserA, slackIdForUserB))

    val serviceStates = for {
      stateAfterCancellingSubscriptionForUserA <- subscriptionCancellationForUserA.runS(
        initialState
      )
      stateAfterCancellingSubscriptionForUserB <- subscriptionCancellationForUserB.runS(
        stateAfterCancellingSubscriptionForUserA
      )
    } yield (stateAfterCancellingSubscriptionForUserA, stateAfterCancellingSubscriptionForUserB)

    serviceStates.map {
      case (stateAfterCancellingSubscriptionForUserA, stateAfterCancellingSubscriptionForUserB) =>
        stateAfterCancellingSubscriptionForUserA.subscriptionKafkaTopic.isEmpty &&
        stateAfterCancellingSubscriptionForUserB.subscriptionKafkaTopic.length == 1 &&
        stateAfterCancellingSubscriptionForUserB.subscriptionKafkaTopic.exists(message =>
          message.repository == repository && message.action == SubscriptionAction.Unsubscribe
        )
    }.assert
  }
}
