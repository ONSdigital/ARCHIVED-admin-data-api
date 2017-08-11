package services

import java.sql.{ Connection, DriverManager, ResultSet, Statement }
import java.util.Optional
import javax.inject.Singleton

import scala.io.Source
import play.api.Logger
import models.{ Company, CompanyConstantsCSV, SearchKeys, Unit }
import com.typesafe.config.Config
import javax.inject.Inject

import uk.gov.ons.sbr.data.controller.AdminDataController
import uk.gov.ons.sbr.data.hbase.HBaseConnector
import uk.gov.ons.sbr.data.domain.CompanyRegistration

import scala.util.{ Failure, Success, Try }

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class CHData @Inject() (implicit val config: Config) {
  val src = config.getString("source")
  val ch = if (src == "csv") readChCSV(config.getString("filename")) else List()

  def getCompanyById(companyNumber: String): Try[List[SearchKeys]] = {
    src match {
      case "csv" => Try(ch.filter(_.CompanyNumber == s""""$companyNumber""""))
      case "hiveLocal" => Try(getCompanyFromDb(companyNumber))
      case "hbaseLocal" => Try(getCompanyFromHbase(companyNumber))
    }
  }

  def optionConverter(o: Optional[CompanyRegistration]): Option[CompanyRegistration] =
    if (o.isPresent) Some(o.get) else None

  def getCompanyFromHbase(companyNumber: String): List[Unit] = {
    HBaseConnector.getInstance().connect()
    val adminController = new AdminDataController()
    val c = adminController.getCompanyRegistration(companyNumber)
    optionConverter(c) match {
      case Some(c) => Unit.mapToUnitList(c)
      case None => List()
    }
  }

  def readChCSV(fileName: String): List[Company] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfLines = Source.fromFile(fileName).getLines.toList
    val listOfLists: List[List[String]] = listOfLines.map(
      _.split(",").toList
    )
    val listOfCaseClasses: List[Company] = listOfLists.map(
      c => Company(
        c(CompanyConstantsCSV.companyName),
        c(CompanyConstantsCSV.companyNumber),
        c(CompanyConstantsCSV.companyCategory),
        c(CompanyConstantsCSV.companyStatus),
        c(CompanyConstantsCSV.countryOfOrigin),
        c(CompanyConstantsCSV.incorporationDate),
        c(CompanyConstantsCSV.addressLine1),
        c(CompanyConstantsCSV.addressLine2),
        c(CompanyConstantsCSV.postTown),
        c(CompanyConstantsCSV.county),
        c(CompanyConstantsCSV.postcode),
        c(CompanyConstantsCSV.accountRefDay),
        c(CompanyConstantsCSV.accountRefMonth),
        c(CompanyConstantsCSV.accountNextDueDate),
        c(CompanyConstantsCSV.accountLastMadeUpDate),
        c(CompanyConstantsCSV.accountCategory),
        c(CompanyConstantsCSV.returnsNextDueDate),
        c(CompanyConstantsCSV.returnsLastMadeUpDate),
        c(CompanyConstantsCSV.sicCodeSicText1),
        c(CompanyConstantsCSV.sicCodeSicText2),
        c(CompanyConstantsCSV.sicCodeSicText3),
        c(CompanyConstantsCSV.sicCodeSicText4)
      )
    )
    Logger.info(s"Loaded in ${listOfCaseClasses.length} companies from CSV file")
    listOfCaseClasses
  }

  def getDbConnection(): Try[Connection] = {
    val url: String = config.getString("url")
    val username: String = config.getString("username")
    val password: String = config.getString("password")
    Class.forName("org.apache.hive.jdbc.HiveDriver")
    Logger.trace(s"Creating JDBC connection with url [${url}]")
    Try(DriverManager.getConnection(url, username, password)).recoverWith {
      case e: Exception => Failure(new Exception("Unable to create JDBC connection to Hive"))
    }
  }

  def getCompanyFromDb(companyNumber: String): List[Company] = {
    getDbConnection() match {
      case Failure(thrown) => {
        Logger.error(s"${thrown.getMessage}")
        throw new Exception(s"${thrown.getMessage}")
      }
      case Success(con) => {
        val cols = List(
          "CompanyName", "CompanyNumber", "CompanyCategory", "CompanyStatus", "CountryOfOrigin", "IncorporationDate",
          "RegAddressAddressLine1", "RegAddressAddressLine2", "RegAddressPostTown", "RegAddressCounty",
          "RegAddressCountry", "AccountsAccountRefDay", "AccountsAccountRefMonth", "AccountsNextDueDate",
          "AccountsLastMadeUpDate", "AccountsAccountCategory", "ReturnsNextDueDate", "ReturnsLastMadeUpDate",
          "SICCodeSicText1", "SICCodeSicText2", "SICCodeSicText3", "SICCodeSicText4"
        ).mkString(", ")
        val query: String = s"""SELECT ${cols} FROM ch WHERE companynumber = '"$companyNumber"' LIMIT 1"""
        Logger.trace(s"Running query [${query}] on Hive database")
        val statement: Statement = con.createStatement
        val rs: ResultSet = statement.executeQuery(query)
        val listOfCompanys = Company.rsToCompanyList(rs)
        con.close
        listOfCompanys
      }
    }
  }
}