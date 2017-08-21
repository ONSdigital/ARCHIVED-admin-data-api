package services

import java.time.YearMonth
import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models._
import play.api.Logger
import utils.Utilities._

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class CSVData @Inject() (implicit val config: Config) extends DataAccess {
  val ch = csvToCaseClass(config.getString("chFilename"), "company")
  val vat = csvToCaseClass(config.getString("vatFilename"), "vat")
  val paye = csvToCaseClass(config.getString("payeFilename"), "paye")

  // TODO: remove asInstanceOf[], bad practice
  def getRecordById(id: String, recordType: String): List[SearchKeys] = recordType match {
    case "company" => ch.filter(_.asInstanceOf[Unit].key == s"$id")
    case "vat" => vat.filter(_.asInstanceOf[Unit].key == s"$id")
    case "paye" => paye.filter(_.asInstanceOf[Unit].key == s"$id")
  }

  def getRecordByIdForPeriod(id: String, period: YearMonth, recordType: String): List[SearchKeys] = {
    if (period.toString == "2017-06") getRecordById(id, recordType) else List()
  }

  def csvToCaseClass(fileName: String, recordType: String): List[SearchKeys] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfCaseClasses: List[SearchKeys] = readCsv(fileName).map(
      c => Unit.mapToCaseClass(c, recordType)
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} $recordType records from CSV file")
    println(listOfCaseClasses.head)
    listOfCaseClasses
  }
}
