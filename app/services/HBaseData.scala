package services

import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models.{ SearchKeys, Unit }
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

  def getCompanyFromHbase(companyNumber: String): List[Unit] = {
    HBaseConnector.getInstance().connect()
    val adminController = new AdminDataController()
    val c = adminController.getCompanyRegistration(companyNumber)
    optionConverter(c) match {
      case Some(c) => Unit.mapToUnitList(c)
      case None => List()
    }
  }
}