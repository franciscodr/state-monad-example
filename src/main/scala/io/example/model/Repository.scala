package io.example.model

import io.example.model.Repository.{Name, Organization}

final case class Repository(organization: Organization, name: Name)

object Repository {
  final case class Organization(value: String) extends AnyVal
  final case class Name(value: String)         extends AnyVal
}
