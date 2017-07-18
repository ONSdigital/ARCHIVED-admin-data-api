package services
import javax.inject.Singleton

import scala.io.Source
import play.api.Logger
import models.CompanyHouse

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

  def readChCSV(fileName: String): List[CompanyHouse] = {
    Logger.info(s"Loading in CSV file: ${fileName}")
    val listOfLines = Source.fromFile(fileName).getLines.toList
    val listOfLists: List[List[String]] = listOfLines.map(
      _.split(",").toList
    )
    val listOfCaseClasses: List[CompanyHouse] = listOfLists.map(
      c => CompanyHouse(c(0), c(1), c(2), c(3), c(4), c(5))
    )
    listOfCaseClasses
  }
}
