package services

import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models._
import utils.Utilities.{ readCsv }
import play.api.Logger

import scala.util.{ Try }

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class VATData @Inject() (implicit val config: Config) {
  val src = config.getString("source")
  val vat = if (src == "csv") readVATCSV(config.getString("vatFilename")) else List()

  def getVATById(vatNumber: String): Try[List[SearchKeys]] = src match {
    case "csv" => Try(vat.filter(_.vatref == s"$vatNumber"))
    case "hiveLocal" => Try(List())
    case "hbaseLocal" => Try(List())
  }

  def readVATCSV(fileName: String): List[VAT] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfCaseClasses: List[VAT] = readCsv(fileName).map(
      v => VAT.stringsToCaseClass(v)
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} VAT records from CSV file")
    listOfCaseClasses
  }
}