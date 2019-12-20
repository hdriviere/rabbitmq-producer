name := "rabbitmq-producer"

version := "1.0"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "1.1.2",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "1.1.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test"
)