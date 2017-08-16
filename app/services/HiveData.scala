package services

import java.sql.{ Connection, DriverManager, ResultSet, Statement }
import java.time.YearMonth
import javax.inject.{ Inject, Singleton }

import com.typesafe.config.Config
import models.{ Company, SearchKeys }
import play.api.Logger

import scala.util.{ Failure, Success, Try }

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class HiveData @Inject() (implicit val config: Config) extends DataAccess {

  def getRecordById(id: String, recordType: String): List[SearchKeys] = recordType match {
    case "company" => getCompanyFromDb(id)
    case "vat" => List()
    case "paye" => List()
  }

  def getRecordByIdForPeriod(id: String, period: YearMonth, recordType: String): List[SearchKeys] = recordType match {
    case "company" => List()
    case "vat" => List()
    case "paye" => List()
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