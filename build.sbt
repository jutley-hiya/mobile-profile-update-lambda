name := "mobile-profile-update"

version := "0.11"

scalaVersion := "2.11.8"

libraryDependencies += "com.amazonaws" % "aws-java-sdk-cloudwatch" % "1.10.76"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-core" % "1.10.76"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "1.2.0"
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.5.2"
libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"
libraryDependencies += "org.scalamock" % "scalamock-scalatest-support_2.11" % "3.2.2" % "test"



javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}