package services

import java.time.YearMonth
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models._
import play.api.Logger
import utils.Utilities._

import scala.concurrent.{ Await, Future }
import scala.io.Source
import scala.util.Try
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class CSVData @Inject() (implicit val config: Config) extends DataAccess {
  val ch = readCSV(config.getString("chFilename"), "company")
  val vat = readCSV(config.getString("vatFilename"), "vat")
  val paye = readCSV(config.getString("payeFilename"), "paye")

  // TODO: remove asInstanceOf[], bad practice
  def getRecordById(id: String, recordType: String): List[SearchKeys] = recordType match {
    case "company" => ch.filter(_.asInstanceOf[Unit].key == s"$id")
    case "vat" => vat.filter(_.asInstanceOf[Unit].key == s"$id")
    case "paye" => paye.filter(_.asInstanceOf[Unit].key == s"$id")
  }

  def getRecordByIdForPeriod(id: String, period: YearMonth, recordType: String): List[SearchKeys] = recordType match {
    case "company" => List()
    case "vat" => List()
    case "paye" => List()
  }

  def readCSV(fileName: String, recordType: String): List[SearchKeys] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfCaseClasses: List[SearchKeys] = readCsvTest(fileName).map(
      c => Unit.mapToCaseClass(c, recordType)
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} $recordType records from CSV file")
    println(listOfCaseClasses.head)
    listOfCaseClasses
  }

  def readCsvTest(fileName: String): List[Map[String, String]] = {
    //    val counter = new AtomicInteger(0)
    //    val src = Source.fromFile(fileName).getLines
    //    val header = src.take(1).next.split(",").toList
    //    val t = src.toList.map(header.zip(_).toMap)
    //    t
    //    val res = src.toList.map { line =>
    //      Future {
    //        val c = counter.incrementAndGet()
    //        if (c % 1000 == 0) Logger.debug(s"Processed 1000 lines of $fileName")
    //        splitCsvLineTest(line, header)
    //      }
    //    }
    //    Await.result(Future.sequence(res), 2 minutes)

    val counter = new AtomicInteger(0)
    val content = Source.fromFile(fileName).getLines.map(_.split(","))
    val header = content.next
    val data = content.map(z => header.zip(z.map(a => a.replaceAll("^\"|\"$", ""))).toMap).toList
    data
  }

  def splitCsvLineTest(line: String, header: List[String]): Map[String, String] = {
    val counter = new AtomicInteger(0)
    val c = line.split(",").toList.map(
      item => {
        // Probably a better way of doing this, recursion etc.
        val i = counter.incrementAndGet()
        println(header(i - 1))
        Map(header(i - 1) -> item.replaceAll("^\"|\"$", ""))
      }
    ).reduce(_ ++ _)
    c
  }
}
