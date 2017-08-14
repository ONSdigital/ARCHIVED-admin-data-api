package services

import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models._
import play.api.Logger
import utils.Utilities._

import scala.util.Try

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
  def getRecordById(id: String, recordType: String): Try[List[SearchKeys]] = recordType match {
    case "company" => Try(ch.filter(_.asInstanceOf[Company].CompanyNumber == s""""$id""""))
    case "vat" => Try(vat.filter(_.asInstanceOf[VAT].vatref == s"$id"))
    case "paye" => Try(paye.filter(_.asInstanceOf[PAYE].payeref == s"$id"))
  }

  def readCSV(fileName: String, recordType: String): List[SearchKeys] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfCaseClasses: List[SearchKeys] = readCsv(fileName).map(
      c => recordType match {
        case "company" => Company.stringsToCaseClass(c)
        case "vat" => VAT.stringsToCaseClass(c)
        case "paye" => PAYE.stringsToCaseClass(c)
      }
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} $recordType records from CSV file")
    listOfCaseClasses
  }
}