package services
import javax.inject.Singleton

import scala.io.Source
import play.api.Logger
import models.Company

/**
 * Created by coolit on 17/07/2017.
 */

/**
 * When the application starts, this will run and load all the CSV data into the Enterprise case class,
 * which will be accessible from the controllers using @Inject().
 */
@Singleton
class LoadCsvData {
  val chCsvPath = "conf/sample/company_house_data.csv"
  val ch = readChCSV(chCsvPath)

  def readChCSV(fileName: String): List[Company] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfLines = Source.fromFile(fileName).getLines.toList
    val listOfLists: List[List[String]] = listOfLines.map(
      _.split(",").toList
    )
    val listOfCaseClasses: List[Company] = listOfLists.map(
      c => Company(c(0), c(1), c(10), c(11), c(12), c(14), c(4), c(5), c(6), c(7), c(8), c(15), c(16), c(17), c(18), c(19), c(20), c(21), c(26), c(27), c(28), c(29))
    )
    listOfCaseClasses
  }
}