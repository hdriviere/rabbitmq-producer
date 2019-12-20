import ProducerApp._
import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.util.ByteString
import org.scalatest.FunSuite

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class TestProducerApp extends FunSuite {
  test("Is the flow working as expected") {
    val sink: Sink[ByteString, Future[String]] = Flow[ByteString].map(_.utf8String).toMat(Sink.fold("")(_ + " " + _))(Keep.right)

    val future = comedyTitlesFlow("src/test/resources/title.basics.test.tsv").runWith(sink)

    val result = Await.result(future, 3 seconds)
    assert(result == " " + "Salome Mad" + " " + "Jarní sen starého mládence")
  }

}
