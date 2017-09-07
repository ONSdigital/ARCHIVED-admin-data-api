package test

import play.api.test.Helpers._

class IntegrationSpec extends TestUtils {

  "Get by company number" should {

    "return correct company" in {
      val companyNumber = "08209948"
      val res = fakeRequest(s"/v1/companies/${companyNumber}")
      status(res) mustBe OK
      contentType(res) mustBe Some("application/json")
      val returnedCrn = getJsValueString(contentAsJson(res) \ "key")
      val addressSize = getJsValueString(contentAsJson(res) \ "vars" \ "CompanyName").length
      returnedCrn must be(companyNumber)
      // Check we have nested JSON for the address
      addressSize must be > 1
    }

    "return 404 if company is not found" in {
      val companyNumber = "12345678"
      val res = fakeRequest(s"/v1/companies/${companyNumber}")
      status(res) mustBe NOT_FOUND
      contentType(res) mustBe Some("application/json")
    }

    //    "return 400 if the query is malformed" in {
    //      val companyNumber = "19384720"
    //      val res = fakeRequest(s"/v1/companies/${companyNumber}")
    //      status(res) mustBe BAD_REQUEST
    //      contentType(res) mustBe Some("application/json")
    //    }
  }
}
