package utils

import java.io.File

import play.api.libs.json._
import play.api.mvc.Result

import scala.concurrent.Future

/**
 * Created by coolit on 18/07/2017.
 */
object Utilities {
  def currentDirectory = new File(".").getCanonicalPath

  def errAsJson(status: Int, code: String, msg: String): JsObject = {
    Json.obj(
      "status" -> status,
      "code" -> code,
      "message_en" -> msg
    )
  }

  def getElement(value: Any) = {
    val res = value match {
      case Some(i: Int) => i
      case Some(l: Long) => l
      case Some(z) => s""""${z}""""
      case x => s""""${x.toString}""""
      case None => ""
    }
    res
  }

  /**
   * Run regex on a string to check for validity
   *
   *  @param toCheck String to check
   *  @param conditions Any number of regex strings
   *  @return boolean
   */
  def checkRegex(toCheck: String, conditions: String*): Boolean =
    conditions
      .toList
      .map(x => toCheck.matches(x))
      .foldLeft(true)(_ || _)

  /**
   * Make results futures, Ok("").future not just Ok("")
   */
  implicit class ResultAugmenter(val res: Result) {
    def future: Future[Result] = {
      Future.successful(res)
    }
  }
}