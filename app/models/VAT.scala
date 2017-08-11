package models

import play.api.libs.json.{ JsValue, Json, Writes }

/**
 * Created by coolit on 18/07/2017.
 */

case class VAT(
  entref: String,
  vatref: String,
  deathcode: String,
  birthdate: String,
  deathdate: String,
  sic92: String,
  turnover: String,
  turnover_date: String,
  record_type: String,
  legalstatus: String,
  actiondate: String,
  crn: String,
  marker: String,
  addressref: String,
  inqcode: String,
  nameline1: String,
  nameline2: String,
  nameline3: String,
  tradstyle1: String,
  tradstyle2: String,
  tradstyle3: String,
  address1: String,
  address2: String,
  address3: String,
  address4: String,
  address5: String,
  postcode: String,
  classType: String = "vat"
) extends SearchKeys

object VAT {
  implicit val writer = new Writes[VAT] {
    def writes(v: VAT): JsValue = {
      Json.obj(
        "entref" -> v.entref,
        "vatref" -> v.vatref,
        "deathcode" -> v.deathcode,
        "birthdate" -> v.birthdate,
        "deathdate" -> v.deathdate,
        "sic92" -> v.sic92,
        "turnover" -> v.turnover,
        "turnover_date" -> v.turnover_date,
        "record_type" -> v.record_type,
        "legalstatus" -> v.legalstatus,
        "actiondate" -> v.actiondate,
        "crn" -> v.crn,
        "marker" -> v.marker,
        "addressref" -> v.addressref,
        "inqcode" -> v.inqcode,
        "nameline1" -> v.nameline1,
        "nameline2" -> v.nameline2,
        "nameline3" -> v.nameline3,
        "tradstyle1" -> v.tradstyle1,
        "tradstyle2" -> v.tradstyle2,
        "tradstyle3" -> v.tradstyle3,
        "address" -> Json.obj(
          "address1" -> v.address1,
          "address2" -> v.address2,
          "address3" -> v.address3,
          "address4" -> v.address4,
          "address5" -> v.address5,
          "postcode" -> v.postcode
        )
      )
    }
  }

  def toJson(vat: VAT): JsValue = Json.toJson(vat)
}

object VATConstantsCSV {
  val entref = 0
  val vatref = 1
  val deathcode = 2
  val birthdate = 3
  val deathdate = 4
  val sic92 = 5
  val turnover = 6
  val turnover_date = 7
  val record_type = 8
  val legalstatus = 9
  val actiondate = 10
  val crn = 11
  val marker = 12
  val addressref = 13
  val inqcode = 14
  val nameline1 = 15
  val nameline2 = 16
  val nameline3 = 17
  val tradstyle1 = 18
  val tradstyle2 = 19
  val tradstyle3 = 20
  val address1 = 21
  val address2 = 22
  val address3 = 23
  val address4 = 24
  val address5 = 25
  val postcode = 26
}