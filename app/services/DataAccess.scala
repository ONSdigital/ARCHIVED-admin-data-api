package services

import models.SearchKeys

import scala.util.Try

/**
 * Created by coolit on 10/08/2017.
 */
trait DataAccess {
  def getRecordById(id: String, recordType: String): Try[List[SearchKeys]]
}
