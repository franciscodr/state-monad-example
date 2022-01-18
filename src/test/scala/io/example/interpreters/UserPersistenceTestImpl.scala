package io.example.interpreters

import cats.data.StateT
import cats.effect.IO
import io.example.algebras.UserPersistence
import io.example.model.{User, UserNotFound}

class UserPersistenceTestImpl extends UserPersistence[StateT[IO, ServiceState, *]] {
  override def addUser(user: User): StateT[IO, ServiceState, Unit] = StateT { serviceState =>
    IO(serviceState.addUser(user), ())
  }

  override def getUserBySlackId(slackId: User.SlackId): StateT[IO, ServiceState, User] = StateT {
    serviceState =>
      IO.fromOption(serviceState.userDbTable.find(_.slackId == slackId))(
        UserNotFound(slackId)
      ).map((serviceState, _))
  }
}
