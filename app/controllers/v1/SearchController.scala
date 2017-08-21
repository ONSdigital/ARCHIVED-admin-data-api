package controllers.v1

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent, Result, Controller }
import utils.Utilities._
import javax.inject.Inject
import java.time.DateTimeException

import com.typesafe.config.Config
import play.api.Logger
import models._
import services._

import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

/**
 * Created by coolit on 18/07/2017.
 */
@Api("Search")
class SearchController @Inject() (data: DataAccess, val config: Config) extends Controller {

  val src = config.getString("source")

  @ApiOperation(
    value = "JSON of the matching company",
    notes = "The company is matched on CompanyNumber",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record found for id."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 422, responseContainer = "JSONObject", message = "Client Side Error -> Wrong companyNumber format."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def getCompanyById(@ApiParam(value = "A valid companyNumber, [A-Z]{2}d{6} or [0-9]{8}", example = "AB123456", required = true) companyNumber: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefById(companyNumber, "company")
    }
  }

  @ApiOperation(
    value = "JSON of the matching VAT record",
    notes = "The VAT record is matched on VAT reference number",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record found for id."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 422, responseContainer = "JSONObject", message = "Client Side Error -> Wrong companyNumber format."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def getVatById(@ApiParam(value = "A valid VAT reference, [0-9]{12}", example = "123456789012", required = true) vatRef: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefById(vatRef, "vat")
    }
  }

  @ApiOperation(
    value = "JSON of the matching PAYE record",
    notes = "The PAYE record is matched on PAYE reference number",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record found for id."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 422, responseContainer = "JSONObject", message = "Client Side Error -> Wrong companyNumber format."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def getPayeById(@ApiParam(value = "A valid PAYE reference, [0-9]{5,13}", example = "12345678", required = true) payeRef: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefById(payeRef, "paye")
    }
  }

  @ApiOperation(
    value = "JSON of the matching company",
    notes = "The company is matched on CompanyNumber",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record found for id."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 422, responseContainer = "JSONObject", message = "Client Side Error -> Wrong companyNumber/period format."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def getCompanyByIdForPeriod(@ApiParam(value = "A valid companyNumber, [A-Z]{2}d{6} or [0-9]{8}", example = "AB123456", required = true) companyNumber: String, @ApiParam(value = "A valid period, YYYYMM", example = "201707", required = true) period: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefByIdForPeriod(companyNumber, "company", period)
    }
  }

  @ApiOperation(
    value = "JSON of the matching VAT record",
    notes = "The company is matched on VAT reference",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record found for id."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 422, responseContainer = "JSONObject", message = "Client Side Error -> Wrong vatRef/period format."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def getVatByIdForPeriod(@ApiParam(value = "A valid vatRef, [0-9]{12}", example = "123456789012", required = true) vatRef: String, @ApiParam(value = "A valid period, YYYYMM", example = "201707", required = true) period: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefByIdForPeriod(vatRef, "vat", period)
    }
  }

  @ApiOperation(
    value = "JSON of the matching PAYE record",
    notes = "The company is matched on PAYE reference",
    responseContainer = "JSONObject",
    code = 200,
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, responseContainer = "JSONObject", message = "Success -> Record found for id."),
    new ApiResponse(code = 404, responseContainer = "JSONObject", message = "Client Side Error -> Id not found."),
    new ApiResponse(code = 422, responseContainer = "JSONObject", message = "Client Side Error -> Wrong payeRef/period format."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def getPayeByIdForPeriod(@ApiParam(value = "A valid payeRef, [0-9]{5,13}", example = "12345", required = true) payeRef: String, @ApiParam(value = "A valid period, YYYYMM", example = "201707", required = true) period: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefByIdForPeriod(payeRef, "paye", period)
    }
  }

  def getRefById(id: String, refType: String): Future[Result] = {
    val src: String = config.getString("source")
    Logger.info(s"Searching for $refType with id: ${id} in source: ${src}")
    id match {
      case id if UnitType.validateUnitId(id, refType) => Try(data.getRecordById(id, refType)) match {
        case Success(results) => resultsMatcher(results, id)
        case Failure(_) => InternalServerError(errAsJson(INTERNAL_SERVER_ERROR, "Internal Server Error", s"An error has occurred, please contact the server administrator")).future
      }
      case _ => UnprocessableEntity(errAsJson(UNPROCESSABLE_ENTITY, "Unprocessable Entity", "Please ensure the vat/ch/paye reference is the correct length/format")).future
    }
  }

  def getRefByIdForPeriod(id: String, refType: String, period: String): Future[Result] = {
    val src: String = config.getString("source")
    Logger.info(s"Searching for $refType with id: ${id}, for period: ${period} in source: ${src}")
    id match {
      case id if UnitType.validateUnitId(id, refType) => Try(periodToYearMonth(period)) match {
        case Success(validPeriod) => Try(data.getRecordByIdForPeriod(id, validPeriod, refType)) match {
          case Success(results) => resultsMatcher(results, id)
          case Failure(_) => InternalServerError(errAsJson(INTERNAL_SERVER_ERROR, "Internal Server Error", s"An error has occurred, please contact the server administrator")).future
        }
        case Failure(_: DateTimeException) => UnprocessableEntity(errAsJson(UNPROCESSABLE_ENTITY, "Unprocessable Entity", "Please ensure the period is in the following format: YYYYMM")).future
        case _ => InternalServerError(errAsJson(INTERNAL_SERVER_ERROR, "Internal Server Error", s"An error has occurred, please contact the server administrator")).future
      }
      case _ => UnprocessableEntity(errAsJson(UNPROCESSABLE_ENTITY, "Unprocessable Entity", "Please ensure the vat/ch/paye reference is the correct length/format")).future
    }
  }

  def resultsMatcher(results: List[SearchKeys], id: String): Future[Result] = results match {
    case Nil => NotFound(errAsJson(404, "Not Found", s"Could not find value ${id}")).future
    case x => x.head match {
      case (c: Company) => Ok(Company.toJson(c)).future
      case (p: PAYE) => Ok(PAYE.toJson(p)).future
      case (v: VAT) => Ok(VAT.toJson(v)).future
      case (u: UnitType) => Ok(UnitType.toJson(u)).future
    }
  }
}