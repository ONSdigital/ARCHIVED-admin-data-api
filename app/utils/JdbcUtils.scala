package utils

import java.sql.ResultSet

/**
 * Created by coolit on 07/08/2017.
 * // https://stackoverflow.com/a/15950556
 */
class RsIterator(rs: ResultSet) extends Iterator[ResultSet] {
  def hasNext: Boolean = rs.next()
  def next(): ResultSet = rs
}
