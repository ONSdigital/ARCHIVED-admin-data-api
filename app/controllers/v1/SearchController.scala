package controllers.v1

import io.swagger.annotations._
import play.api.mvc.{ Action, AnyContent, Result }
import utils.Utilities._
import javax.inject.Inject
import java.time.{ DateTimeException, YearMonth }
import java.time.format.DateTimeFormatter

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
class SearchController @Inject() (data: DataAccess, val config: Config) extends ControllerUtils {

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
    new ApiResponse(code = 422, responseContainer = "JSONObject", message = "Client Side Error -> Wrong companyNumber format."),
    new ApiResponse(code = 500, responseContainer = "JSONObject", message = "Server Side Error -> Request could not be completed.")
  ))
  def getCompanyByIdForPeriod(@ApiParam(value = "A valid companyNumber, [A-Z]{2}d{6} or [0-9]{8}", example = "AB123456", required = true) companyNumber: String, period: String): Action[AnyContent] = {
    Action.async { implicit request =>
      getRefByIdForPeriod(companyNumber, "company", period)
    }
  }

  def getRefById(id: String, refType: String): Future[Result] = {
    val src: String = config.getString("source")
    Logger.info(s"Searching for $refType with id: ${id} in source: ${src}")
    val res = id match {
      case id if validateId(id, refType) => data.getRecordById(id, refType) match {
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

  def getRefByIdForPeriod(id: String, refType: String, period: String): Future[Result] = {
    val src: String = config.getString("source")
    Logger.info(s"Searching for $refType with id: ${id} in source: ${src}")
    val res = id match {
      case id if validateId(id, refType) => periodToYearMonth(period) match {
        case validPeriod => data.getRecordByIdForPeriod(id, validPeriod, refType) match {
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
        } // DateTimeException
        case _ => UnprocessableEntity(errAsJson(422, "Unprocessable Entity", "Please ensure the period is in the following format: YYYYMM")).future
      }
      case _ => UnprocessableEntity(errAsJson(422, "Unprocessable Entity", "Please ensure the vat/ch/paye reference is the correct length/format")).future
    }
    res
  }

  def periodToYearMonth(period: String): YearMonth = {
    YearMonth.parse(period.slice(0, 6), DateTimeFormatter.ofPattern("yyyyMM"))
  }

  def validateId(id: String, refType: String): Boolean = refType match {
    case "company" => checkRegex(id, "[A-Z]{2}\\d{6}", "[0-9]{8}")
    case "paye" => checkRegex(id, "[0-9]{5,13}")
    case "vat" => checkRegex(id, "[0-9]{12}")
  }
}