package services

import java.io.File
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

  val firstPeriod = "201706"
  val ch = csvToCaseClass(new File(s"conf/sample/sbr-2500-ent-ch-data.csv").toURI.toURL.toExternalForm, "company")
  val vat = csvToCaseClass(new File(s"conf/sample/${firstPeriod}/vat_data.csv").toURI.toURL.toExternalForm, "vat")
  val paye = csvToCaseClass(new File(s"conf/sample/${firstPeriod}/paye_data.csv").toURI.toURL.toExternalForm, "paye")

  def getRecordById(id: String, recordType: String): List[SearchKeys] = recordType match {
    case "company" => ch.filter { case (a: UnitType) => a.key == s"$id" }
    case "vat" => vat.filter { case (a: UnitType) => a.key == s"$id" }
    case "paye" => paye.filter { case (a: UnitType) => a.key == s"$id" }
  }

  def getRecordByIdForPeriod(id: String, period: YearMonth, recordType: String): List[SearchKeys] = {
    if (period.toString == "2017-06") getRecordById(id, recordType) else List()
  }

  def csvToCaseClass(fileName: String, recordType: String): List[SearchKeys] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val period = config.getString("period")
    val listOfCaseClasses: List[SearchKeys] = readCsv(fileName).map(
      c => UnitType.mapToCaseClass(c, recordType, period)
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} $recordType records from CSV file")
    listOfCaseClasses
  }
}
