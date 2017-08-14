package models

import java.util

import play.api.libs.json.{ JsValue, Json, Writes }
import uk.gov.ons.sbr.data.domain.CompanyRegistration

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