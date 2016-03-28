name := """Scala IRC"""

version := "1.0"

scalaVersion := "2.11.8"

lazy val akkaVersion = "2.4.2"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scala-lang" % "jline" % "2.10.3"
)
