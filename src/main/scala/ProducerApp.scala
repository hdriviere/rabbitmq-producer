import java.nio.file.Paths

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{IOResult, OverflowStrategy}
import akka.stream.alpakka.amqp.scaladsl.AmqpSink
import akka.stream.alpakka.amqp.{AmqpLocalConnectionProvider, AmqpWriteSettings, QueueDeclaration}
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object ProducerApp {
  implicit val actorSystem: ActorSystem = ActorSystem()
  import actorSystem.dispatcher

  def main(args: Array[String]): Unit = {
    comedyTitlesFlow("src/main/resources/title.basics.tsv")
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

  private def createAmqpSink: Sink[ByteString, Future[Done]] = {
    val connectionProvider = AmqpLocalConnectionProvider
    val queueName = "amqp-queue-" + System.currentTimeMillis()
    val queueDeclaration = QueueDeclaration(queueName)

    AmqpSink.simple(
      AmqpWriteSettings(connectionProvider)
        .withRoutingKey(queueName)
        .withDeclaration(queueDeclaration)
    )
  }
}