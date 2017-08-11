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
    val listOfLists = readCsv(fileName)
    val listOfCaseClasses: List[VAT] = listOfLists.map(
      v => VAT(
        v(VATConstantsCSV.entref),
        v(VATConstantsCSV.vatref),
        v(VATConstantsCSV.deathcode),
        v(VATConstantsCSV.birthdate),
        v(VATConstantsCSV.deathdate),
        v(VATConstantsCSV.sic92),
        v(VATConstantsCSV.turnover),
        v(VATConstantsCSV.turnover_date),
        v(VATConstantsCSV.record_type),
        v(VATConstantsCSV.legalstatus),
        v(VATConstantsCSV.actiondate),
        v(VATConstantsCSV.crn),
        v(VATConstantsCSV.marker),
        v(VATConstantsCSV.addressref),
        v(VATConstantsCSV.inqcode),
        v(VATConstantsCSV.nameline1),
        v(VATConstantsCSV.nameline2),
        v(VATConstantsCSV.nameline3),
        v(VATConstantsCSV.tradstyle1),
        v(VATConstantsCSV.tradstyle2),
        v(VATConstantsCSV.tradstyle3),
        v(VATConstantsCSV.address1),
        v(VATConstantsCSV.address2),
        v(VATConstantsCSV.address3),
        v(VATConstantsCSV.address4),
        v(VATConstantsCSV.address5),
        v(VATConstantsCSV.postcode)
      )
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} VAT records from CSV file")
    listOfCaseClasses
  }
}