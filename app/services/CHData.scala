package services

import java.sql.{ Connection, DriverManager, ResultSet, Statement }
import javax.inject.Singleton

import scala.io.Source
import play.api.Logger
import models.Company
import utils.RsIterator

import com.typesafe.config.Config
import javax.inject.Inject

/**
 * Created by coolit on 07/08/2017.
 * When the application starts, this will be accessible in the controllers through the use of @Inject()
 */
@Singleton
class CHData @Inject() (implicit val config: Config) {
  val src = config.getString("source")
  val ch = if (src == "csv") readChCSV(config.getString("filename")) else List()

  def getCompanyById(companyNumber: String): List[Company] = {
    src match {
      case "csv" => ch.filter(_.CompanyNumber == s""""$companyNumber"""")
      case "hiveLocal" => getCompanyFromDb(companyNumber)
    }
  }

  def readChCSV(fileName: String): List[Company] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfLines = Source.fromFile(fileName).getLines.toList
    val listOfLists: List[List[String]] = listOfLines.map(
      _.split(",").toList
    )
    val listOfCaseClasses: List[Company] = listOfLists.map(
      c => Company(c(0), c(1), c(10), c(11), c(12), c(14), c(4), c(5), c(6), c(7), c(8), c(15), c(16), c(17), c(18), c(19), c(20), c(21), c(26), c(27), c(28), c(29))
    )
    listOfCaseClasses
  }

  def getCompanyFromDb(companyNumber: String): List[Company] = {
    val url: String = config.getString("url")
    val username: String = config.getString("username")
    val password: String = config.getString("password")
    val query: String = s"""SELECT * FROM ch WHERE companynumber = '"$companyNumber"' LIMIT 1"""

    Logger.trace(s"Running query [${query}] on url [${url}] with username: ${username}")

    try {
      Class.forName("org.apache.hive.jdbc.HiveDriver")
      val connection: Connection = DriverManager.getConnection(url, username, password)
      val statement: Statement = connection.createStatement
      val rs: ResultSet = statement.executeQuery(query)
      val listOfCompanies: List[Company] = new RsIterator(rs).map(x => {
        Company(
          x.getString(1), // CompanyName
          x.getString(2), // CompanyNumber
          x.getString(11), // CompanyCategory
          x.getString(12), // CompanyStatus
          x.getString(13), // CountryOfOrigin
          x.getString(15), // IncorporationDate
          // Address
          x.getString(5), // AddressLine1
          x.getString(6), // AddressLine2
          x.getString(7), // PostTown
          x.getString(8), // County
          x.getString(9), // Postcode
          // Accounts
          x.getString(16), // AccountRefDay
          x.getString(17), // AccountRefMonth
          x.getString(18), // AccountNextDueDate
          x.getString(19), // AccountLastMadeUpDate
          x.getString(20), // AccountCategory
          // Returns
          x.getString(21), // ReturnsNextDueDate
          x.getString(22), // ReturnsLastMadeUpDate
          // Sic
          x.getString(27), // SICCodeSicText1
          x.getString(28), // SICCodeSicText2
          x.getString(29), // SICCodeSicText3
          x.getString(30) // SICCodeSicText4
        )
      }).toList
      connection.close
      listOfCompanies
    } catch {
      case e: Exception => {
        Logger.info(s"Database exception: ${e.toString}")
        List()
      }
    }
  }
}