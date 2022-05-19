import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "todo-api",
    libraryDependencies ++= Seq(
      airframe,
      scalaTest % Test
    )
  )


  enablePlugins(PackPlugin)
  packMain := Map("todo" -> "org.sflip.todo_api.Main")


// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
