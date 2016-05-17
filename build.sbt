import sbt._

name := "mobile-profile-update-lambda"

organization := "com.hiya"

scalaVersion := "2.11.8"

resolvers += "Hiya Artifacts" at "s3://hiya-artifact-repository"

// Main
libraryDependencies += "com.hiya" % "mobile-profile-update_2.11" % "0.1.2"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "1.2.0"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"

// IT (and some Test)
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "it,test"
libraryDependencies += "org.scalamock" % "scalamock-scalatest-support_2.11" % "3.2.2" % "it,test"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-lambda" % "1.10.77" % "it"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.10.77" % "it"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.10.77" % "it"

// aws-lambda-java libraries pull in a bunch of incompatable versions (1.11.x) which we have to replace
// Note: These are still compatable. Minimum is 1.10.5
dependencyOverrides += "com.amazonaws" % "aws-java-sdk-cognitoidentity" % "1.10.77" % "it"
dependencyOverrides += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.10.77" % "it"
dependencyOverrides += "com.amazonaws" % "aws-java-sdk-kinesis" % "1.10.77" % "it"
dependencyOverrides += "com.amazonaws" % "aws-java-sdk-s3" % "1.10.77" % "it"
dependencyOverrides += "com.amazonaws" % "aws-java-sdk-sns" % "1.10.77" % "it"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}

// Stuff for EndToEndTest
Project.inConfig(IntegrationTest)(baseAssemblySettings)

assemblyJarName in (IntegrationTest, assembly) := s"${name.value}-it-assembly-${version.value}.jar"

