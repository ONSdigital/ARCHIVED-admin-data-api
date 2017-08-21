package services

import java.time.YearMonth
import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models.SearchKeys
import org.apache.hadoop.util.ToolRunner
import play.api.Logger
import uk.gov.ons.sbr.data.controller.AdminDataController
import uk.gov.ons.sbr.data.domain.UnitType
import uk.gov.ons.sbr.data.hbase.load.BulkLoader
import uk.gov.ons.sbr.data.hbase.HBaseConnector
import utils.Utilities._

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class HBaseData @Inject() (val loadData: Boolean, val config: Config) extends DataAccess {

  HBaseConnector.getInstance().connect()
  private val adminController = new AdminDataController()

  if (loadData) loadHBaseData()

  def loadHBaseData(): Unit = {
    Logger.info("Loading local CSVs into In-Memory HBase...")
    val bulkLoader = new BulkLoader()
    val period = config.getString("period")
    List(
      List[String](UnitType.COMPANY_REGISTRATION.toString, period, config.getString("chFilename")),
      List[String](UnitType.VAT.toString, period, config.getString("vatFilename")),
      List[String](UnitType.PAYE.toString, period, config.getString("payeFilename"))
    ).foreach(arg => {
        Logger.info(s"Loading CSV [${arg(2)}] into HBase for period [${arg(1)}] and type [${arg(0)}]...")
        ToolRunner.run(HBaseConnector.getInstance().getConfiguration(), bulkLoader, arg.toArray)
      })
  }

  def getRecordById(id: String, recordType: String): List[SearchKeys] = getRecordFromHbase(id, recordType)

  def getRecordByIdForPeriod(id: String, period: YearMonth, recordType: String): List[SearchKeys] =
    getRecordFromHbaseForPeriod(id, period, recordType)

  def getRecordFromHbase(id: String, recordType: String): List[models.UnitType] = {
    val record = recordType match {
      case "company" => adminController.getCompanyRegistration(id)
      case "vat" => adminController.getVATReturn(id)
      case "paye" => adminController.getPAYEReturn(id)
    }
    val z = record.toOption.toUnitList
    z
  }

  def getRecordFromHbaseForPeriod(id: String, period: YearMonth, recordType: String): List[models.UnitType] = {
    val record = recordType match {
      case "company" => adminController.getCompanyRegistrationForReferencePeriod(period, id)
      case "vat" => adminController.getVATReturnForReferencePeriod(period, id)
      case "paye" => adminController.getPAYEReturnForReferencePeriod(period, id)
    }
    record.toOption.toUnitList
  }
}