import java.nio.file.Paths

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{IOResult, OverflowStrategy}
import akka.stream.alpakka.amqp.scaladsl.AmqpSink
import akka.stream.alpakka.amqp.{AmqpLocalConnectionProvider, AmqpUriConnectionProvider, AmqpWriteSettings, QueueDeclaration}
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl._
import akka.util.ByteString
import scala.util.Properties


import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object ProducerApp {
  implicit val actorSystem: ActorSystem = ActorSystem()
  import actorSystem.dispatcher

  def main(args: Array[String]): Unit = {
    comedyTitlesFlow(scala.util.Properties.envOrElse("FILE_TO_PROCESS", "src/main/resources/title.basics.tsv"))
      .runWith(createAmqpSink)
      .andThen {
        case _ =>
          actorSystem.terminate()
          Await.ready(actorSystem.whenTerminated, 1 minute)
      }
  }

  def comedyTitlesFlow(path: String): Source[ByteString, Future[IOResult]] = {
    FileIO.fromPath(Paths.get(path))
      //.buffer(100, OverflowStrategy.backpressure) if necessary we can use a buffer to avoid consumer overflow
      .via(CsvParsing.lineScanner('\t'))
      .via(CsvToMap.toMapAsStrings())
      .filter(dict => dict("titleType").contains("movie") && dict("genres").contains("Comedy"))
      .map(dict => ByteString(dict("primaryTitle")))
  }

  private def produceUri: String = {
    val username = scala.util.Properties.envOrElse("RABBITMQ_USERNAME", "guest" )
    val password = scala.util.Properties.envOrElse("RABBITMQ_PASSWORD", "guest" )
    val host = scala.util.Properties.envOrElse("RABBITMQ_HOST", "localhost" )
    val port = scala.util.Properties.envOrElse("RABBITMQ_PORT", "5672" )
    "amqp://" + username + ":" + password + "@" + host + ":" + port
  }

  private def createAmqpSink: Sink[ByteString, Future[Done]] = {
    val connectionProvider = AmqpUriConnectionProvider(produceUri)
    val queueName = "amqp-queue-" + System.currentTimeMillis()
    val queueDeclaration = QueueDeclaration(queueName)

    AmqpSink.simple(
      AmqpWriteSettings(connectionProvider)
        .withRoutingKey(queueName)
        .withDeclaration(queueDeclaration)
    )
  }
}