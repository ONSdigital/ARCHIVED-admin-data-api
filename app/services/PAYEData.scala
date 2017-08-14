package services

import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models._
import play.api.Logger
import utils.Utilities.readCsv

import scala.util.Try

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class PAYEData @Inject() (implicit val config: Config) {
  val src = config.getString("source")
  val paye = if (src == "csv") readPAYECSV(config.getString("payeFilename")) else List()

  def getPAYEById(payeRef: String): Try[List[SearchKeys]] = src match {
    case "csv" => Try(paye.filter(_.payeref == s"$payeRef"))
    case "hiveLocal" => Try(List())
    case "hbaseLocal" => Try(List())
  }

  def readPAYECSV(fileName: String): List[PAYE] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfCaseClasses: List[PAYE] = readCsv(fileName).map(
      p => PAYE.stringsToCaseClass(p)
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} PAYE records from CSV file")
    listOfCaseClasses
  }
}