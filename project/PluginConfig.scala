import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._
import sbtbuildinfo.BuildInfoKeys._
import sbtbuildinfo.BuildInfoPlugin
import sbtbuildinfo.BuildInfoPlugin._

// See: http://www.scala-sbt.org/release/docs/Testing.html#Integration+Tests

object PluginConfig extends Build {
  lazy val root =
    Project("mobile-profile-update-lambda", file("."))
      .configs( IntegrationTest )
      .settings( Defaults.itSettings : _*)

      .enablePlugins(BuildInfoPlugin)
      .settings(
        buildInfoKeys := Seq[BuildInfoKey](name, version, assemblyOutputPath in (IntegrationTest, assembly)),
        buildInfoPackage := "com.hiya.mobileprofile.lambda.sbtinfo"
      )
}