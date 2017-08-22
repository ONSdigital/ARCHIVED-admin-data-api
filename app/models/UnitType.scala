package models

import java.util

import play.api.libs.json.{ JsValue, Json, Writes }
import uk.gov.ons.sbr.data.domain.StatisticalUnit
import utils.Utilities._

import scala.collection.JavaConversions._

/**
 * Created by coolit on 10/08/2017.
 */

case class UnitType(
  key: String,
  period: String,
  unitType: String,
  vars: util.Map[String, String],
  classType: String = "unit"
) extends SearchKeys

object UnitType {
  implicit val writer = new Writes[UnitType] {
    def writes(u: UnitType): JsValue = {
      Json.obj(
        "key" -> u.key,
        "period" -> u.period,
        "unitType" -> u.unitType,
        "vars" -> Json.toJson(u.vars.toMap)
      )
    }
  }

  def validateUnitId(id: String, unitType: String): Boolean = unitType match {
    case "company" => checkRegex(id, "[A-Z]{2}\\d{6}", "[0-9]{8}")
    case "paye" => checkRegex(id, "[A-Z0-9]{5,13}")
    case "vat" => checkRegex(id, "[0-9]{12}")
  }

  def toJson(u: UnitType): JsValue = Json.toJson(u)

  def mapToUnitType(unit: StatisticalUnit): UnitType = {
    UnitType(
      unit.getKey,
      unit.getReferencePeriod.toString,
      unit.getType.toString,
      unit.getVariables
    )
  }

  def mapToCaseClass(unit: Map[String, String], recordType: String, period: String): UnitType = {
    // TODO: deal with Options/Maps better here?
    UnitType(
      recordType match {
        case "company" => unit.get("CompanyNumber").getOrElse("")
        case "vat" => unit.get("vatref").getOrElse("")
        case "paye" => unit.get("payeref").getOrElse("")
      },
      period,
      recordType,
      unit
    )
  }
}