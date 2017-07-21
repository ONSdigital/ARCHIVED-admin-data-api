package controllers.v1

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent }
import utils.Utilities._
import com.outworkers.util.play._
import javax.inject.Inject

import com.typesafe.config.Config
import play.api.Logger
import models.CompanyHouseObj
import services.LoadCsvData
import play.api.db._
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.deploy.SparkHadoopUtil
import org.apache.hadoop.fs.{ FileSystem, Path }

import org.apache.spark.sql.SQLContext

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

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
  def getCompanyById(@ApiParam(value = "An identifier of any type", example = "87395710", required = true) id: Option[String]): Action[AnyContent] = {
    Action.async { implicit request =>
      Logger.info(s"Searching for company with id: ${id}")
      val res = id match {
        case Some(id) if id.length > 0 => loadCsvData.ch.filter(_.company_number == id) match {
          case Nil => NotFound(errAsJson(404, "not found", s"Could not find value ${id}")).future
          case x => Ok(CompanyHouseObj.toJson(x(0))).future
        }
        case _ => BadRequest(errAsJson(400, "missing parameter", "No query string found")).future
      }
      res
    }
  }

  def test(): Action[AnyContent] = {
    Action.async { implicit request =>
      Class.forName("org.apache.hive.jdbc.HiveDriver");
      Logger.info(s"Getting db connection...")
      val con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default", "raj_ops", "password");
      val stmt = con.createStatement();
      val sql = "SELECT * FROM sample_07";
      System.out.println("Running: " + sql);
      val res = stmt.executeQuery(sql);
      while (res.next()) {
        println(res.getString(1), res.getString(2), res.getString(3));
      }
      Ok("Done.").future
    }
  }
}