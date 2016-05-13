name := "mobile-profile-update-lambda"

organization := "com.hiya"

version := "0.0.1"

scalaVersion := "2.11.8"

resolvers += "Hiya Artifacts" at "s3://hiya-artifact-repository"

libraryDependencies += "org.scalamock" % "scalamock-scalatest-support_2.11" % "3.2.2" % "test"



javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}
