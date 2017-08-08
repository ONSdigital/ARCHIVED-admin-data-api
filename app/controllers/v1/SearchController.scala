package controllers.v1

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent }
import utils.Utilities._
import com.outworkers.util.play._
import javax.inject.Inject

import com.typesafe.config.Config
import play.api.Logger
import models.{ CompanyObj }
import services.{ CHData }

/**
 * Created by coolit on 18/07/2017.
 */
@Api("Search")
class SearchController @Inject() (chData: CHData, val config: Config) extends ControllerUtils {

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
  def getCompanyById(@ApiParam(value = "An identifier of any type", example = "87395710", required = true) companyNumber: String): Action[AnyContent] = {
    Action.async { implicit request =>
      val src: String = config.getString("source")
      Logger.info(s"Searching for company with id: ${companyNumber} in source: ${src}")
      val res = companyNumber match {
        case companyNumber if companyNumber.length > 0 => chData.getCompanyById(companyNumber) match {
          case Nil => NotFound(errAsJson(404, "not found", s"Could not find value ${companyNumber}")).future
          case _ :: _ :: Nil => InternalServerError(errAsJson(500, "internal server error", s"more than one result returned for companyNumber: $companyNumber")).future
          case x => {
            Logger.info(s"Returning company [${companyNumber}]: ${x.head}")
            Ok(CompanyObj.toJson(x.head)).future
          }
        }
        case _ => BadRequest(errAsJson(400, "missing parameter", "No query string found")).future
      }
      res
    }
  }
}