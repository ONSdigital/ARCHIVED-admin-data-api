package models
import java.sql.ResultSet

import play.api.libs.json.{ JsValue, Json, Writes }
import utils.RsIterator

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
  AddressLine1: String,
  AddressLine2: String,
  PostTown: String,
  County: String,
  Postcode: String,
  AccountRefDay: String,
  AccountRefMonth: String,
  AccountNextDueDate: String,
  AccountLastMadeUpDate: String,
  AccountCategory: String,
  ReturnsNextDueDate: String,
  ReturnsLastMadeUpDate: String,
  SICCodeSicText1: String,
  SICCodeSicText2: String,
  SICCodeSicText3: String,
  SICCodeSicText4: String,
  classType: String = "company"
) extends SearchKeys

object Company {
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

  def toJson(company: Company): JsValue = Json.toJson(company)

  def rsToCompanyList(rs: ResultSet): List[Company] = {
    new RsIterator(rs).map(x => {
      Company(
        x.getString(CompanyConstants.companyName),
        x.getString(CompanyConstants.companyNumber),
        x.getString(CompanyConstants.companyCategory),
        x.getString(CompanyConstants.companyStatus),
        x.getString(CompanyConstants.countryOfOrigin),
        x.getString(CompanyConstants.incorporationDate),
        x.getString(CompanyConstants.addressLine1),
        x.getString(CompanyConstants.addressLine2),
        x.getString(CompanyConstants.postTown),
        x.getString(CompanyConstants.county),
        x.getString(CompanyConstants.postcode),
        x.getString(CompanyConstants.accountRefDay),
        x.getString(CompanyConstants.accountRefMonth),
        x.getString(CompanyConstants.accountNextDueDate),
        x.getString(CompanyConstants.accountLastMadeUpDate),
        x.getString(CompanyConstants.accountCategory),
        x.getString(CompanyConstants.returnsNextDueDate),
        x.getString(CompanyConstants.returnsLastMadeUpDate),
        x.getString(CompanyConstants.sicCodeSicText1),
        x.getString(CompanyConstants.sicCodeSicText2),
        x.getString(CompanyConstants.sicCodeSicText3),
        x.getString(CompanyConstants.sicCodeSicText4)
      )
    }).toList
  }
}

object CompanyConstants {
  val companyName = 1
  val companyNumber = 2
  val companyCategory = 3
  val companyStatus = 4
  val countryOfOrigin = 5
  val incorporationDate = 6
  val addressLine1 = 7
  val addressLine2 = 8
  val postTown = 9
  val county = 10
  val postcode = 11
  val accountRefDay = 12
  val accountRefMonth = 13
  val accountNextDueDate = 14
  val accountLastMadeUpDate = 15
  val accountCategory = 16
  val returnsNextDueDate = 17
  val returnsLastMadeUpDate = 18
  val sicCodeSicText1 = 19
  val sicCodeSicText2 = 20
  val sicCodeSicText3 = 21
  val sicCodeSicText4 = 22
}