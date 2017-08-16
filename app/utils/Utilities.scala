package utils

import java.io.File
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Optional
import java.util.concurrent.atomic.AtomicInteger

import play.api.Logger
import play.api.libs.json._
import play.api.mvc.Result

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.io.Source

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
  def checkRegex(toCheck: String, conditions: String*): Boolean = conditions.toList
    .map(x => toCheck.matches(x))
    .foldLeft(false)(_ || _)

  /**
   * Method source: https://github.com/outworkers/util/blob/develop/util-play/src/main/scala/com/outworkers/util/play/package.scala#L98
   */
  implicit class ResultAugmenter(val res: Result) {
    def future: Future[Result] = {
      Future.successful(res)
    }
  }

  // Source: https://github.com/ONSdigital/business-index-api/blob/develop/api/app/uk/gov/ons/bi/CsvProcessor.scala#L30
  def readCsv(fileName: String): List[List[String]] = {
    val counter = new AtomicInteger(0)
    val res = Source.fromFile(fileName).getLines.drop(1).toList.map { line =>
      Future {
        val c = counter.incrementAndGet()
        if (c % 1000 == 0) Logger.debug(s"Processed 1000 lines of $fileName")
        splitCsvLine(line)
      }
    }
    Await.result(Future.sequence(res), 2 minutes)
  }

  def splitCsvLine(line: String): List[String] = {
    line.split(",").toList.map(
      // Remove leading and trailing double qoutes (only present on the CH csv)
      s => s.replaceAll("^\"|\"$", "")
    )
  }

  def optionConverter[T](o: Optional[T]): Option[T] = if (o.isPresent) Some(o.get) else None

  def periodToYearMonth(period: String): YearMonth = {
    YearMonth.parse(period.slice(0, 6), DateTimeFormatter.ofPattern("yyyyMM"))
  }
}