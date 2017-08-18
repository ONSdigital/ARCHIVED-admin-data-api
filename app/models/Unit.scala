package models

import java.util

import play.api.libs.json.{ JsValue, Json, Writes }
import uk.gov.ons.sbr.data.domain.{ CompanyRegistration, StatisticalUnit }
import utils.Utilities._

import scala.collection.JavaConversions._

/**
 * Created by coolit on 10/08/2017.
 */

case class Unit(
  key: String,
  period: String,
  unitType: String,
  vars: util.Map[String, String],
  classType: String = "unit"
) extends SearchKeys

object Unit {
  implicit val writer = new Writes[Unit] {
    def writes(u: Unit): JsValue = {
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
    case "paye" => checkRegex(id, "[0-9]{5,13}")
    case "vat" => checkRegex(id, "[0-9]{12}")
  }

  def toJson(u: Unit): JsValue = Json.toJson(u)

  def mapToUnitList(unit: StatisticalUnit): List[Unit] = {
    List(
      Unit(
        unit.getKey,
        unit.getReferencePeriod.toString,
        unit.getType.toString,
        unit.getVariables
      )
    )
  }

  def mapToCaseClass(unit: Map[String, String], recordType: String): Unit = {
    // TODO: deal with Options/Maps better here?
    val id = recordType match {
      case "company" => unit.get("CompanyNumber").get
      case "vat" => unit.get("vatref").get
      case "paye" => unit.get("payeref").get
    }
    Unit(
      id,
      "201706",
      recordType,
      unit
    )
  }
}