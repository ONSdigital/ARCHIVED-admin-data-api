package services

import java.time.YearMonth
import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models.{ SearchKeys, Unit }
import org.apache.hadoop.util.ToolRunner
import uk.gov.ons.sbr.data.controller.AdminDataController
import uk.gov.ons.sbr.data.domain.UnitType
import uk.gov.ons.sbr.data.hbase.load.BulkLoader
import uk.gov.ons.sbr.data.hbase.{ HBaseConnector, HBaseTest }
import utils.Utilities._

import scala.io.Source
import scala.util.Try

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class HBaseDataInMemory @Inject() (implicit val config: Config) extends DataAccess {

  //HBaseTest.init()
  // HBaseTestUtilities.startMiniCluster
  // val bulkLoader = new BulkLoader()
  //  val unitType = UnitType.COMPANY_REGISTRATION.toString
  //  val args = Array[String](unitType, "201701", "conf/sample/company_house_data.csv")
  //  ToolRunner.run(HBaseConnector.getInstance().getConfiguration(), bulkLoader, args)

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