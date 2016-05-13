import sbt._

// See: http://www.scala-sbt.org/release/docs/Testing.html#Integration+Tests

object ITConfig extends Build {
  lazy val root =
    Project("mobile-profile-update-lambda", file("."))
      .configs( IntegrationTest )
      .settings( Defaults.itSettings : _*)
}