package models

import java.util

import play.api.libs.json.{ JsValue, Json, Writes }
import uk.gov.ons.sbr.data.domain.CompanyRegistration

/**
 * Created by coolit on 10/08/2017.
 */

case class Unit(
  key: String,
  period: String,
  unitType: String,
  vars: util.Map[String, String],
  classType: String = "unit"
) extends models.MyAbstract

object UnitObj {
  implicit val writer = new Writes[Unit] {
    def writes(u: Unit): JsValue = {
      val sicText = List(u.vars.get("siccode_sictext_1"), u.vars.get("siccode_sictext_2"), u.vars.get("siccode_sictext_3"), u.vars.get("siccode_sictext_4")).filter(
        _ != null
      )
      // We use a similar JSON format to the one used by CompanyHouse, found here: /models/ch.json
      Json.obj(
        "CompanyName" -> u.vars.get("companyname"),
        "CompanyNumber" -> u.vars.get("companynumber"),
        "CompanyCategory" -> u.vars.get("companycategory"),
        "CompanyStatus" -> u.vars.get("companystatus"),
        "CountryOfOrigin" -> u.vars.get("countryoforigin"),
        "IncorporationDate" -> u.vars.get("incorporationdate"),
        "Address" -> Json.obj(
          "AddressLine1" -> u.vars.get("regaddress_addressline1"),
          "AddressLine2" -> u.vars.get("regaddress_addressline2"),
          "PostTown" -> u.vars.get("regaddress_posttown"),
          "County" -> u.vars.get("regaddress_county"),
          "Postcode" -> u.vars.get("regaddress_postcode")
        ),
        "Accounts" -> Json.obj(
          "AccountRefDay" -> u.vars.get("accounts_accountrefday"),
          "AccountRefMonth" -> u.vars.get("accounts_accountrefmonth"),
          "AccountNextDueDate" -> u.vars.get("accounts_nextduedate"),
          "AccountLastMadeUpDate" -> u.vars.get("accounts_lastmadeupdate"),
          "AccountCategory" -> u.vars.get("accounts_accountcategory")
        ),
        "Returns" -> Json.obj(
          "ReturnsNextDueDate" -> u.vars.get("returns_nextduedate"),
          "ReturnsLastMadeUpDate" -> u.vars.get("returns_lastmadeupdate")
        ),
        "SICCodes" -> Json.obj(
          "SicText" -> sicText
        )
      )
    }
  }

  def toJson(u: Unit): JsValue = Json.toJson(u)

  def mapToUnitList(unit: CompanyRegistration): List[Unit] = {
    List(
      Unit(
        unit.getKey,
        unit.getReferencePeriod.toString,
        unit.getType.toString,
        unit.getVariables
      )
    )
  }
}