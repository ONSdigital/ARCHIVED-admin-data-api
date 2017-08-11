package models

import play.api.libs.json.{ JsValue, Json, Writes }

/**
 * Created by coolit on 11/08/2017.
 */

case class PAYE(
  entref: String,
  payeref: String,
  deathcode: String,
  birthdate: String,
  deathdate: String,
  mfullemp: String,
  msubemp: String,
  ffullemp: String,
  fsubemp: String,
  unclemp: String,
  unclsubemp: String,
  dec_jobs: String,
  mar_jobs: String,
  june_jobs: String,
  sept_jobs: String,
  jobs_lastupd: String,
  legalstatus: String,
  prevpaye: String,
  employer_cat: String,
  stc: String,
  crn: String,
  actiondate: String,
  addressref: String,
  marker: String,
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
  classType: String = "paye"
) extends SearchKeys

object PAYE {
  implicit val writer = new Writes[PAYE] {
    def writes(p: PAYE): JsValue = {
      Json.obj(
        "entref" -> p.entref,
        "payeref" -> p.payeref,
        "deathcode" -> p.deathcode,
        "birthdate" -> p.birthdate,
        "deathdate" -> p.deathdate,
        "mfullemp" -> p.mfullemp,
        "msubemp" -> p.msubemp,
        "ffullemp" -> p.ffullemp,
        "fsubemp" -> p.fsubemp,
        "unclemp" -> p.unclemp,
        "unclsubemp" -> p.unclsubemp,
        "dec_jobs" -> p.dec_jobs,
        "mar_jobs" -> p.mar_jobs,
        "june_jobs" -> p.june_jobs,
        "sept_jobs" -> p.sept_jobs,
        "jobs_lastupd" -> p.jobs_lastupd,
        "legalstatus" -> p.legalstatus,
        "prevpaye" -> p.prevpaye,
        "employer_cat" -> p.employer_cat,
        "stc" -> p.stc,
        "crn" -> p.crn,
        "actiondate" -> p.actiondate,
        "addressref" -> p.addressref,
        "marker" -> p.marker,
        "inqcode" -> p.inqcode,
        "nameline1" -> p.nameline1,
        "nameline2" -> p.nameline2,
        "nameline3" -> p.nameline3,
        "tradstyle1" -> p.tradstyle1,
        "tradstyle2" -> p.tradstyle2,
        "tradstyle3" -> p.tradstyle3,
        "address" -> Json.obj(
          "address1" -> p.address1,
          "address2" -> p.address2,
          "address3" -> p.address3,
          "address4" -> p.address4,
          "address5" -> p.address5,
          "postcode" -> p.postcode
        )
      )
    }
  }

  def toJson(paye: PAYE): JsValue = Json.toJson(paye)
}

object PAYEConstantsCSV {
  val entref = 0
  val payeref = 1
  val deathcode = 2
  val birthdate = 3
  val deathdate = 4
  val mfullemp = 5
  val msubemp = 6
  val ffullemp = 7
  val fsubemp = 8
  val unclemp = 9
  val unclsubemp = 10
  val dec_jobs = 11
  val mar_jobs = 12
  val june_jobs = 13
  val sept_jobs = 14
  val jobs_lastupd = 15
  val legalstatus = 16
  val prevpaye = 17
  val employer_cat = 18
  val stc = 19
  val crn = 20
  val actiondate = 21
  val addressref = 22
  val marker = 23
  val inqcode = 24
  val nameline1 = 25
  val nameline2 = 26
  val nameline3 = 27
  val tradstyle1 = 28
  val tradstyle2 = 29
  val tradstyle3 = 30
  val address1 = 31
  val address2 = 32
  val address3 = 33
  val address4 = 34
  val address5 = 35
  val postcode = 36
}