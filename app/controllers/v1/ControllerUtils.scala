package controllers.v1

import com.typesafe.config.Config
import play.api.mvc.{ Controller, Result }
import com.typesafe.scalalogging.StrictLogging
import play.Logger

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

trait ControllerUtils extends Controller with StrictLogging {

  @tailrec
  final protected def buildErrMsg(x: Throwable, msgs: List[String] = Nil): String = {
    Option(x.getCause) match {
      case None => (x.getMessage :: msgs).reverse.mkString(" ")
      case Some(ex) => buildErrMsg(ex, x.getMessage :: msgs)
    }
  }

  protected[this] def errAsResponse(f: => Future[Result]): Future[Result] = Try(f) match {
    case Success(g) => g
    case Failure(err) =>
      Logger.error("Unable to produce response.", err)
      Future.successful {
        InternalServerError(s"{err = '${buildErrMsg(err)}'}")
      }
  }
}
