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
    val listOfLists = readCsv(fileName)
    val listOfCaseClasses: List[PAYE] = listOfLists.map(
      p => PAYE(
        p(PAYEConstantsCSV.entref),
        p(PAYEConstantsCSV.payeref),
        p(PAYEConstantsCSV.deathcode),
        p(PAYEConstantsCSV.birthdate),
        p(PAYEConstantsCSV.deathdate),
        p(PAYEConstantsCSV.mfullemp),
        p(PAYEConstantsCSV.msubemp),
        p(PAYEConstantsCSV.ffullemp),
        p(PAYEConstantsCSV.fsubemp),
        p(PAYEConstantsCSV.unclemp),
        p(PAYEConstantsCSV.unclsubemp),
        p(PAYEConstantsCSV.dec_jobs),
        p(PAYEConstantsCSV.mar_jobs),
        p(PAYEConstantsCSV.june_jobs),
        p(PAYEConstantsCSV.sept_jobs),
        p(PAYEConstantsCSV.jobs_lastupd),
        p(PAYEConstantsCSV.legalstatus),
        p(PAYEConstantsCSV.prevpaye),
        p(PAYEConstantsCSV.employer_cat),
        p(PAYEConstantsCSV.stc),
        p(PAYEConstantsCSV.crn),
        p(PAYEConstantsCSV.actiondate),
        p(PAYEConstantsCSV.addressref),
        p(PAYEConstantsCSV.marker),
        p(PAYEConstantsCSV.inqcode),
        p(PAYEConstantsCSV.nameline1),
        p(PAYEConstantsCSV.nameline2),
        p(PAYEConstantsCSV.nameline3),
        p(PAYEConstantsCSV.tradstyle1),
        p(PAYEConstantsCSV.tradstyle2),
        p(PAYEConstantsCSV.tradstyle3),
        p(PAYEConstantsCSV.address1),
        p(PAYEConstantsCSV.address2),
        p(PAYEConstantsCSV.address3),
        p(PAYEConstantsCSV.address4),
        p(PAYEConstantsCSV.address5),
        p(PAYEConstantsCSV.postcode)
      )
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} PAYE records from CSV file")
    listOfCaseClasses
  }
}