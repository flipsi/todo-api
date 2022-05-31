import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest"      %% "scalatest"         % "3.2.11"
  lazy val airframe  = "org.wvlet.airframe" %% "airframe-launcher" % "22.4.2"

  lazy val doobieVersion  = "1.0.0-RC1"
  lazy val doobieCore     = "org.tpolecat" %% "doobie-core"     % doobieVersion
  lazy val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieVersion
  lazy val doobieSpecs    = "org.tpolecat" %% "doobie-specs2"   % doobieVersion
}
