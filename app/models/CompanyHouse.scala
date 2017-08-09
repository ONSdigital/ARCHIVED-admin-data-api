package models
import java.sql.ResultSet
import java.util

import play.api.libs.json.{ JsValue, Json, Writes }
import utils.RsIterator
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

  def companyNumberValidator(cn: String): Boolean = cn.matches("[A-Z]{2}\\d{6}") || cn.matches("[0-9]{8}")

  def toJson(company: Company): JsValue = {
    Json.toJson(
      company
    )
  }

  def mapToCompayList(company: util.Map[String, String]): List[Company] = {
    List(
      Company(
        company.get("companyname"), // CompanyName
        company.get("companynumber"), // CompanyNumber
        company.get("companycategory"), // CompanyCategory
        company.get("companystatus"), // CompanyStatus
        company.get("countryoforigin"), // CountryOfOrigin
        company.get("incorporationdate"), // IncorporationDate
        // Address
        company.get("regaddress_addressline1"), // AddressLine1
        company.get("regaddress_addressline2"), // AddressLine2
        company.get("regaddress_posttown"), // PostTown
        company.get("regaddress_county"), // County
        company.get("regaddress_postcode"), // Postcode
        // Accounts
        company.get("accounts_accountrefday"), // AccountRefDay
        company.get("accounts_accountrefmonth"), // AccountRefMonth
        company.get("accounts_nextduedate"), // AccountNextDueDate
        company.get("accounts_lastmadeupdate"), // AccountLastMadeUpDate
        company.get("accounts_accountcategory"), // AccountCategory
        // Returns
        company.get("returns_nextduedate"), // ReturnsNextDueDate
        company.get("returns_lastmadeupdate"), // ReturnsLastMadeUpDate
        // Sic
        company.get("siccode_sictext_1"), // SICCodeSicText1
        company.get("siccode_sictext_2"), // SICCodeSicText2
        company.get("siccode_sictext_3"), // SICCodeSicText3
        company.get("siccode_sictext_4") // SICCodeSicText4
      )
    )
  }

  def rsToCompanyList(rs: ResultSet): List[Company] = {
    new RsIterator(rs).map(x => {
      Company(
        x.getString(1), // CompanyName
        x.getString(2), // CompanyNumber
        x.getString(3), // CompanyCategory
        x.getString(4), // CompanyStatus
        x.getString(5), // CountryOfOrigin
        x.getString(6), // IncorporationDate
        // Address
        x.getString(7), // AddressLine1
        x.getString(8), // AddressLine2
        x.getString(9), // PostTown
        x.getString(10), // County
        x.getString(11), // Postcode
        // Accounts
        x.getString(12), // AccountRefDay
        x.getString(13), // AccountRefMonth
        x.getString(14), // AccountNextDueDate
        x.getString(15), // AccountLastMadeUpDate
        x.getString(16), // AccountCategory
        // Returns
        x.getString(17), // ReturnsNextDueDate
        x.getString(18), // ReturnsLastMadeUpDate
        // Sic
        x.getString(19), // SICCodeSicText1
        x.getString(20), // SICCodeSicText2
        x.getString(21), // SICCodeSicText3
        x.getString(22) // SICCodeSicText4
      )
    }).toList
  }
}