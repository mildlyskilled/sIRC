name := """Scala IRC"""

version := "1.0"

scalaVersion := "2.11.8"

lazy val akkaVersion = "2.4.2"

libraryDependencies ++= Seq(
  // Change this to another test framework if you prefer
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "jline" % "jline" % "2.13",
  "joda-time" % "joda-time" % "2.9.2"
)
