package services

import java.io.File
import java.time.YearMonth
import javax.inject.{Inject, Singleton}

import com.typesafe.config.Config

import models.{SearchKeys, UnitType}
import org.apache.hadoop.util.ToolRunner
import play.api.Logger

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
    val firstPeriod = "201706"
    val secondPeriod = "201708"
    List(
      List[String](UnitType.COMPANY_REGISTRATION.toString, firstPeriod, new File(s"conf/sample/sbr-2500-ent-ch-data.csv").toURI.toURL.toExternalForm),
      List[String](UnitType.VAT.toString, firstPeriod, new File(s"conf/sample/${firstPeriod}/vat_data.csv").toURI.toURL.toExternalForm),
      List[String](UnitType.PAYE.toString, firstPeriod, new File(s"conf/sample/${firstPeriod}/paye_data.csv").toURI.toURL.toExternalForm),
      List[String](UnitType.COMPANY_REGISTRATION.toString, secondPeriod, new File(s"conf/sample/sbr-2500-ent-ch-data.csv").toURI.toURL.toExternalForm),
      List[String](UnitType.VAT.toString, secondPeriod, new File(s"conf/sample/${secondPeriod}/vat_data.csv").toURI.toURL.toExternalForm),
      List[String](UnitType.PAYE.toString, secondPeriod, new File(s"conf/sample/${secondPeriod}/paye_data.csv").toURI.toURL.toExternalForm)
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