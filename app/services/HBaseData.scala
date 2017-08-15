package services

import java.time.YearMonth
import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models.{ SearchKeys, Unit }
import play.api.Logger
import uk.gov.ons.sbr.data.controller.AdminDataController
import uk.gov.ons.sbr.data.hbase.HBaseConnector
import utils.Utilities._

import scala.util.Try

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class HBaseData @Inject() (implicit val config: Config) extends DataAccess {

  def getRecordById(id: String, recordType: String): Try[List[SearchKeys]] = recordType match {
    case "company" => Try(getCompanyFromHbase(id))
    case "vat" => Try(List())
    case "paye" => Try(List())
  }

  def getRecordByIdForPeriod(id: String, period: YearMonth, recordType: String): Try[List[SearchKeys]] = recordType match {
    case "company" => Try(getCompanyFromHbaseForPeriod(id, period))
    case "vat" => Try(List())
    case "paye" => Try(List())
  }

  def getCompanyFromHbase(companyNumber: String): List[Unit] = {
    HBaseConnector.getInstance().connect()
    val adminController = new AdminDataController()
    val company = adminController.getCompanyRegistration(companyNumber)
    optionConverter(company) match {
      case Some(c) => Unit.mapToUnitList(c)
      case None => List()
    }
  }

  def getCompanyFromHbaseForPeriod(companyNumber: String, period: YearMonth): List[Unit] = {
    HBaseConnector.getInstance().connect()
    val adminController = new AdminDataController()
    val company = adminController.getCompanyRegistrationForReferencePeriod(period, companyNumber)
    optionConverter(company) match {
      case Some(c) => Unit.mapToUnitList(c)
      case None => List()
    }
  }
}