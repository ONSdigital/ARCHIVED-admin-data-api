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

  def loadHBaseData(): scala.Unit = {
    Logger.info("Loading local CSVs into In-Memory HBase...")
    val bulkLoader = new BulkLoader()
    List(
      List[String](UnitType.COMPANY_REGISTRATION.toString, "201706", "conf/sample/sbr-2500-ent-ch-data.csv"),
      List[String](UnitType.VAT.toString, "201706", "conf/sample/vat_data.csv"),
      List[String](UnitType.PAYE.toString, "201706", "conf/sample/paye_data.csv")
    ).foreach(arg => {
        Logger.info(s"Loading CSV [${arg(2)}] into HBase for period [${arg(1)}] and type [${arg(0)}]...")
        ToolRunner.run(HBaseConnector.getInstance().getConfiguration(), bulkLoader, arg.toArray)
      })
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