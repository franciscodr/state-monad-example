package io.example.interpreters

import cats.data.StateT
import cats.effect.IO
import io.example.algebras.UserPersistence
import io.example.model.{User, UserNotFound}

class UserPersistenceTestImpl extends UserPersistence[StateT[IO, ServiceState, *]] {
  override def addUser(user: User): StateT[IO, ServiceState, Unit] =
    StateT.modify(state => state.addUser(user))

  override def getUserBySlackId(slackId: User.SlackId): StateT[IO, ServiceState, User] =
    StateT.inspectF(state =>
      IO.fromOption(state.userDbTable.find(_.slackId == slackId))(UserNotFound(slackId))
    )
}
