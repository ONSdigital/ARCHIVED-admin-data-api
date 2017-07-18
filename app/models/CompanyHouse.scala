package models
import play.api.libs.json.{ JsValue, Json, Writes }
import utils.Utilities.{ errAsJson, getElement }

import scala.util.parsing.json.{ JSONArray, JSONObject }

/**
 * Created by coolit on 18/07/2017.
 */

case class CompanyHouse(
  company_number: String,
  company_name: String,
  address_line_1: String,
  country: String,
  postal_code: String,
  region: String
)

case class CompanyHouseJson(
  company_number: String,
  company_name: String,
  address: CompanyHouseAddress
)

case class CompanyHouseAddress(
  address_line_1: String,
  country: String,
  postal_code: String,
  region: String
)

object CompanyHouseObj {

  implicit val writer = new Writes[CompanyHouseJson] {
    def writes(t: CompanyHouseJson): JsValue = {
      Json.obj(
        "company_number" -> t.company_number,
        "company_name" -> t.company_name,
        "address" -> Json.obj(
          "address_line_1" -> t.address.address_line_1,
          "country" -> t.address.country,
          "postal_code" -> t.address.postal_code,
          "region" -> t.address.region
        )
      )
    }
  }

  def toJson(company: CompanyHouse): JsValue = {
    //val address = CompanyHouseAddress(company.address_line_1, company.country, company.postal_code, company.region)
    //val ch = CompanyHouseJson(company.company_number, company.company_name, address)
    Json.toJson(
      CompanyHouseJson(
        company.company_number,
        company.company_name,
        CompanyHouseAddress(
          company.address_line_1,
          company.country,
          company.postal_code,
          company.region
        )
      )
    )
  }
}