package services

import java.time.YearMonth

import models.SearchKeys

import scala.util.Try

/**
 * Created by coolit on 10/08/2017.
 */
trait DataAccess {
  def getRecordById(id: String, recordType: String): Try[List[SearchKeys]]
  def getRecordByIdForPeriod(id: String, period: YearMonth, recordType: String): Try[List[SearchKeys]]
}
