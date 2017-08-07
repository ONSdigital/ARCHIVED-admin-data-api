package models
import play.api.libs.json.{ JsValue, Json, Writes }
import utils.Utilities.{ errAsJson, getElement }

import scala.util.parsing.json.{ JSONArray, JSONObject }

/**
 * Created by coolit on 18/07/2017.
 */

case class Company(
  CompanyName: String,
  CompanyNumber: String,
  CompanyCategory: String,
  CompanyStatus: String,
  CountryOfOrigin: String,
  IncorporationDate: String,
  // Address
  AddressLine1: String,
  AddressLine2: String,
  PostTown: String,
  County: String,
  Postcode: String,
  // Accounts
  AccountRefDay: String,
  AccountRefMonth: String,
  AccountNextDueDate: String,
  AccountLastMadeUpDate: String,
  AccountCategory: String,
  // Returns
  ReturnsNextDueDate: String,
  ReturnsLastMadeUpDate: String,
  // SIC
  SICCodeSicText1: String,
  SICCodeSicText2: String,
  SICCodeSicText3: String,
  SICCodeSicText4: String
)

object CompanyObj {
  implicit val writer = new Writes[Company] {
    def writes(c: Company): JsValue = {
      val sicText = List(c.SICCodeSicText1, c.SICCodeSicText2, c.SICCodeSicText3, c.SICCodeSicText4).filter(
        _ != "\"\""
      )
      // We use a similar JSON format to the one used by CompanyHouse, found here: /models/ch.json
      Json.obj(
        "CompanyName" -> c.CompanyName,
        "CompanyNumber" -> c.CompanyNumber,
        "CompanyCategory" -> c.CompanyCategory,
        "CompanyStatus" -> c.CompanyStatus,
        "CountryOfOrigin" -> c.CountryOfOrigin,
        "IncorporationDate" -> c.IncorporationDate,
        "Address" -> Json.obj(
          "AddressLine1" -> c.AddressLine1,
          "AddressLine2" -> c.AddressLine2,
          "PostTown" -> c.PostTown,
          "County" -> c.County,
          "Postcode" -> c.Postcode
        ),
        "Accounts" -> Json.obj(
          "AccountRefDay" -> c.AccountRefDay,
          "AccountRefMonth" -> c.AccountRefMonth,
          "AccountNextDueDate" -> c.AccountNextDueDate,
          "AccountLastMadeUpDate" -> c.AccountLastMadeUpDate,
          "AccountCategory" -> c.AccountCategory
        ),
        "Returns" -> Json.obj(
          "ReturnsNextDueDate" -> c.ReturnsNextDueDate,
          "ReturnsLastMadeUpDate" -> c.ReturnsLastMadeUpDate
        ),
        "SICCodes" -> Json.obj(
          "SicText" -> sicText
        )
      )
    }
  }

  def toJson(company: Company): JsValue = {
    Json.toJson(
      Company(
        company.CompanyName,
        company.CompanyNumber,
        company.CompanyCategory,
        company.CompanyStatus,
        company.CountryOfOrigin,
        company.IncorporationDate,
        company.AddressLine1,
        company.AddressLine2,
        company.PostTown,
        company.County,
        company.Postcode,
        company.AccountRefDay,
        company.AccountRefMonth,
        company.AccountNextDueDate,
        company.AccountLastMadeUpDate,
        company.AccountCategory,
        company.ReturnsNextDueDate,
        company.ReturnsLastMadeUpDate,
        company.SICCodeSicText1,
        company.SICCodeSicText2,
        company.SICCodeSicText3,
        company.SICCodeSicText4
      )
    )
  }
}