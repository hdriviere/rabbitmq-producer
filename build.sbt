name := "rabbitmq-producer"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.1",
  "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "1.1.2",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.1" % "test"
)