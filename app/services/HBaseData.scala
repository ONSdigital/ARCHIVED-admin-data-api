package services

import java.time.YearMonth
import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models.{ SearchKeys, Unit }
import org.apache.hadoop.util.ToolRunner
import play.api.Logger
import uk.gov.ons.sbr.data.controller.AdminDataController
import uk.gov.ons.sbr.data.domain.UnitType
import uk.gov.ons.sbr.data.hbase.load.BulkLoader
import uk.gov.ons.sbr.data.hbase.{ HBaseConnector, HBaseTest }
import utils.Utilities._

import scala.util.Try

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class HBaseData @Inject() (val loadData: Boolean, val config: Config) extends DataAccess {

  HBaseConnector.getInstance().connect()
  private val adminController = new AdminDataController()

  if (loadData) loadHBaseData()

  def loadHBaseData(): scala.Unit = {
    Logger.info("Loading HBase data...")
    HBaseTest.init()
    val bulkLoader = new BulkLoader()
    val unitType = UnitType.COMPANY_REGISTRATION.toString
    val args = Array[String](unitType, "201701", "conf/sample/company_house_data.csv")
    ToolRunner.run(HBaseConnector.getInstance().getConfiguration(), bulkLoader, args)
  }

  def getRecordById(id: String, recordType: String): List[SearchKeys] = getRecordFromHbase(id, recordType)

  def getRecordByIdForPeriod(id: String, period: YearMonth, recordType: String): List[SearchKeys] =
    getRecordFromHbaseForPeriod(id, period, recordType)

  def getRecordFromHbase(id: String, recordType: String): List[Unit] = {
    val record = recordType match {
      case "company" => adminController.getCompanyRegistration(id)
      case "vat" => adminController.getVATReturn(id)
      case "paye" => adminController.getPAYEReturn(id)
    }
    optionConverter(record) match {
      case Some(c) => Unit.mapToUnitList(c)
      case None => List()
    }
  }

  def getRecordFromHbaseForPeriod(id: String, period: YearMonth, recordType: String): List[Unit] = {
    val record = recordType match {
      case "company" => adminController.getCompanyRegistrationForReferencePeriod(period, id)
      case "vat" => adminController.getVATReturnForReferencePeriod(period, id)
      case "paye" => adminController.getPAYEReturnForReferencePeriod(period, id)
    }
    optionConverter(record) match {
      case Some(c) => Unit.mapToUnitList(c)
      case None => List()
    }
  }
}