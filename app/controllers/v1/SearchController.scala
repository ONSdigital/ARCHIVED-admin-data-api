package controllers.v1

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent, Result }
import utils.Utilities._
import javax.inject.Inject

import com.typesafe.config.Config
import play.api.Logger
import models._
import services.{ CHData, PAYEData, VATData }

import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

/**
 * Created by coolit on 18/07/2017.
 */
@Api("Search")
class SearchController @Inject() (chData: CHData, vatData: VATData, payeData: PAYEData, val config: Config) extends ControllerUtils {

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
      getRefById(companyNumber, "company")
    }
  }

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
  def getVatById(@ApiParam(value = "An identifier of any type", example = "87395710", required = true) vatRef: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefById(vatRef, "vat")
    }
  }

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
  def getPayeById(@ApiParam(value = "An identifier of any type", example = "87395710", required = true) payeRef: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefById(payeRef, "paye")
    }
  }

  def getRefById(id: String, refType: String): Future[Result] = {
    val src: String = config.getString("source")
    Logger.info(s"Searching for $refType with id: ${id} in source: ${src}")
    val res = id match {
      case id if validateId(id, refType) => getRefFromSource(id, refType) match {
        case Success(results) => results match {
          case Nil => NotFound(errAsJson(404, "Not Found", s"Could not find value ${id}")).future
          case x => x.head match {
            case (c: Company) => Ok(Company.toJson(c)).future
            case (p: PAYE) => Ok(PAYE.toJson(p)).future
            case (v: VAT) => Ok(VAT.toJson(v)).future
            case (u: Unit) => Ok(Unit.toJson(u)).future
          }
        }
        case Failure(e) => InternalServerError(errAsJson(500, "Internal Server Error", s"An error has occurred, please contact the server administrator")).future
      }
      case _ => UnprocessableEntity(errAsJson(422, "Unprocessable Entity", "Please ensure the vat/ch/paye reference is the correct length/format")).future
    }
    res
  }

  def getRefFromSource(id: String, refType: String): Try[List[SearchKeys]] = refType match {
    case "company" => chData.getCompanyById(id)
    case "paye" => payeData.getPAYEById(id)
    case "vat" => vatData.getVATById(id)
  }

  def validateId(id: String, refType: String): Boolean = refType match {
    case "company" => checkRegex(id, "[A-Z]{2}\\d{6}", "[0-9]{8}")
    case "paye" => checkRegex(id, "[0-9]{5,13}")
    case "vat" => checkRegex(id, "[0-9]{12}")
  }
}