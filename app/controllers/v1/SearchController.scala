package controllers.v1

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent }
import utils.Utilities._
import com.outworkers.util.play._
import javax.inject.Inject

import com.typesafe.config.Config
import play.api.Logger
import models.{ Company, CompanyObj }
import services.LoadCsvData
import java.sql.{ Connection, DriverManager, ResultSet, Statement }

import utils.RsIterator

/**
 * Created by coolit on 18/07/2017.
 */
@Api("Search")
class SearchController @Inject() (loadCsvData: LoadCsvData, val config: Config) extends ControllerUtils {

  @ApiOperation(
    value = "JSON of the matching company",
    notes = "The company is matched only on id",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record found for id."),
    new ApiResponse(code = 400, responseContainer = "JSONObject", message = "Client Side Error -> Required parameter was not found."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def getCompanyByIdCSV(@ApiParam(value = "An identifier of any type", example = "87395710", required = true) companyNumber: Option[String]): Action[AnyContent] = {
    Action.async { implicit request =>
      Logger.info(s"Searching for company with id: ${companyNumber}")
      val res = companyNumber match {
        case Some(companyNumber) if companyNumber.length > 0 => loadCsvData.ch.filter(_.CompanyNumber == s""""$companyNumber"""") match {
          case Nil => NotFound(errAsJson(404, "not found", s"Could not find value ${companyNumber}")).future
          case _ :: _ :: Nil => InternalServerError(errAsJson(500, "internal server error", s"more than one result returned for companyNumber: $companyNumber")).future
          case x => Ok(CompanyObj.toJson(x(0))).future
        }
        case _ => BadRequest(errAsJson(400, "missing parameter", "No query string found")).future
      }
      res
    }
  }

  def getCompanyById(companyNumber: Option[String]): Action[AnyContent] = {
    Action.async { implicit request =>
      Logger.info(s"Searching for company with id: ${companyNumber}")
      val res = companyNumber match {
        case Some(companyNumber) if companyNumber.length > 0 => getCompanyFromDb(companyNumber) match {
          case Nil => NotFound(errAsJson(404, "not found", s"Could not find value ${companyNumber}")).future
          case _ :: _ :: Nil => InternalServerError(errAsJson(500, "internal server error", s"more than one result returned for companyNumber: $companyNumber")).future
          case x => Ok(CompanyObj.toJson(x.head)).future
        }
        case _ => BadRequest(errAsJson(400, "missing parameter", "No query string found")).future
      }
      res
    }
  }

  def getCompanyFromDb(companyNumber: String): List[Company] = {
    val url: String = "jdbc:hive2://localhost:10000/default"
    val driver: String = "org.apache.hive.jdbc.HiveDriver"
    val username: String = "raj_ops"
    val password: String = "password"
    val query: String = s"""SELECT * FROM ch WHERE companynumber = '"$companyNumber"' LIMIT 1"""

    try {
      Class.forName(driver)
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
        Logger.info(e.toString)
        List()
      }
    }
  }
}