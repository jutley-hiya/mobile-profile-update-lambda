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

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}

