package controllers.v1

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent }
import utils.Utilities._
import com.outworkers.util.play._
import javax.inject.Inject

import com.typesafe.config.Config
import play.api.Logger
import models.{ CompanyHouseAddress, CompanyHouseJson, CompanyHouseObj }
import services.LoadCsvData
import play.api.db._
import org.apache.hadoop.fs.{ FileSystem, Path }
import org.apache.spark.sql.SQLContext
import java.sql.SQLException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.sql.DriverManager

import play.api.libs.json.{ JsValue, Json, Writes };

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
        case Some(companyNumber) if companyNumber.length > 0 => loadCsvData.ch.filter(_.company_number == companyNumber) match {
          case Nil => NotFound(errAsJson(404, "not found", s"Could not find value ${companyNumber}")).future
          case x => Ok(CompanyHouseObj.toJson(x(0))).future
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
    val query: String = s"""SELECT * FROM company_house WHERE companynumber = '"$companyNumber"'"""

    try {
      Class.forName(driver)
      val connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement
      val rs = statement.executeQuery(query)
      rs.next
      val name = rs.getString(1)
      val id = rs.getString(2)
      connection.close
      List(Company(id, name))
    } catch {
      case e: Exception => {
        Logger.info(e.toString)
        List()
      }
    }
  }
}

case class Company(
  company_number: String,
  company_name: String
)

object CompanyObj {
  implicit val writer = new Writes[Company] {
    def writes(t: Company): JsValue = {
      Json.obj(
        "company_number" -> t.company_number,
        "company_name" -> t.company_name
      )
    }
  }

  def toJson(company: Company): JsValue = {
    Json.toJson(
      Company(
        company.company_number,
        company.company_name
      )
    )
  }
}