package controllers.v1

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent }
import utils.Utilities._
import com.outworkers.util.play._
import javax.inject.Inject

import play.api.Logger
import models.CompanyHouseObj
import services.LoadCsvData

/**
 * Created by coolit on 18/07/2017.
 */
@Api("Search")
class SearchController @Inject() (loadCsvData: LoadCsvData) extends ControllerUtils {

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
}