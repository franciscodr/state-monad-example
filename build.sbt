ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "test-playground",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit"               % "0.7.29",
      "org.typelevel" %% "cats-core"           % "2.7.0",
      "org.typelevel" %% "cats-effect"         % "3.3.4",
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % "test"
    ),
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
  )
