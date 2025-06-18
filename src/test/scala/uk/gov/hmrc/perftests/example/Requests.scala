/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.example

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object Requests extends ServicesConfiguration {

  val baseUrl: String     = baseUrlFor("crs-fatca-fi-management-frontend")
  val baseUrlAuth: String = baseUrlFor("auth-frontend")
  val route: String       = "/manage-your-crs-and-fatca-financial-institutions"
  val authRoute: String   = "/auth-login-stub/gg-sign-in"
  val amazonUrlPattern    = """action="(.*?)""""

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  val getAuthLoginPage: HttpRequestBuilder =
    http("Get Auth login page")
      .get(baseUrlAuth + authRoute)
      .check(status.is(200))

  val postAuthLoginCredentials: HttpRequestBuilder =
    http("Enter Auth login credentials")
      .post(baseUrlAuth + authRoute)
      .formParam("authorityId", "")
      .formParam("redirectionUrl", baseUrl + route)
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("enrolment[0].name", "HMRC-FATCA-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", "FATCAID")
      .formParam("enrolment[0].taxIdentifier[0].value", "XE9ATCA0009234567")
      .formParam("enrolment[0].state", "Activated")
      .check(status.is(303))
      .check(header("Location").is(baseUrl + route).saveAs("LandingPage"))

  val getCRSFATCADashboardPage: HttpRequestBuilder =
    http("Get CRS FATCA Dashboard Page")
      .get("${LandingPage}")
      .check(status.is(200))

  val getAddFiPageRedirect: HttpRequestBuilder =
    http("Get Add FI Redirect")
      .get("${LandingPage}/add")
      .check(status.is(303))
      .check(header("Location").saveAs("addFiPageUrl"))

  val getAddFiFormPage: HttpRequestBuilder =
    http("Get Add FI Form Page")
      .get(baseUrl+"${addFiPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))
      .check(bodyString.saveAs("responseBody"))

  val postAddFiForm: HttpRequestBuilder =
    http("Submit Add FI Form")
      .post(baseUrl + "/manage-your-crs-and-fatca-financial-institutions/name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "My Financial Institution")
      .check(status.is(303))
      .check(header("Location").saveAs("IdentificationNumbersPageUrl"))

  val getIdentificationNumbersPage: HttpRequestBuilder =
    http("Get Identification-Numbers Page")
      .get(baseUrl + "${IdentificationNumbersPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postIdentificationNumbersPage: HttpRequestBuilder =
    http("Submit Identification-Numbers")
      .post(baseUrl + "${IdentificationNumbersPageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value[0]", "UTR")
      .check(status.is(303))
      .check(header("Location").saveAs("UtrPageUrl"))

  val getUtrPage: HttpRequestBuilder =
    http("Get UTR Page")
      .get(baseUrl + "${UtrPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postUtrPage: HttpRequestBuilder =
    http("Submit UTR")
      .post(baseUrl + "${UtrPageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "1234567890")
      .check(status.is(303))
      .check(header("Location").saveAs("HaveGiinPageUrl"))

  val getHaveGiinPage: HttpRequestBuilder =
    http("Get Have-GIIN Page")
      .get(baseUrl + "${HaveGiinPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveGiinPage: HttpRequestBuilder =
    http("Submit Have-GIIN (Yes)")
      .post(baseUrl + "${HaveGiinPageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").saveAs("GiinPageUrl"))

  val getGiinPage: HttpRequestBuilder =
    http("Get GIIN Page")
      .get(baseUrl + "${GiinPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postGiinPage: HttpRequestBuilder =
    http("Submit GIIN")
      .post(baseUrl + "${GiinPageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "98096B.00000.LE.350")
      .check(status.is(303))
      .check(header("Location").saveAs("AddressUkPageUrl"))

  val getAddressUkPage: HttpRequestBuilder =
    http("Get UK-Address Page")
      .get(baseUrl + "${AddressUkPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postAddressUkPage: HttpRequestBuilder =
    http("Submit UK-Address")
      .post(baseUrl +  route + "/address-uk")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("addressLine1", "Test Street")
      .formParam("addressLine2", "")
      .formParam("addressLine3", "Test City")
      .formParam("addressLine4", "")
      .formParam("postCode", "TF3 7BT")
      .check(status.is(303))
      .check(header("Location").saveAs("ContactNamePageUrl"))

  val getContactNamePage: HttpRequestBuilder =
    http("Get Contact-Name Page")
      .get(baseUrl + "${ContactNamePageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postContactNamePage: HttpRequestBuilder =
    http("Submit Contact-Name")
      .post(baseUrl + route + "/contact-name")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "James Bond 007")
      .check(status.is(303))
      .check(header("Location").saveAs("ContactEmailPageUrl"))

  val getContactEmailPage: HttpRequestBuilder =
    http("Get Contact-Email Page")
      .get(baseUrl + "${ContactEmailPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postContactEmailPage: HttpRequestBuilder =
    http("Submit Contact-Email")
      .post(baseUrl + "${ContactEmailPageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "test@test.com")
      .check(status.is(303))
      .check(header("Location").saveAs("HavePhonePageUrl"))

  val getHavePhonePage: HttpRequestBuilder =
    http("Get Have-Phone Page")
      .get(baseUrl + "${HavePhonePageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHavePhonePage: HttpRequestBuilder =
    http("Submit Have-Phone (Yes)")
      .post(baseUrl + "${HavePhonePageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").saveAs("PhonePageUrl"))

  val getPhonePage: HttpRequestBuilder =
    http("Get Phone Page")
      .get(baseUrl + "${PhonePageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postPhonePage: HttpRequestBuilder =
    http("Submit Phone")
      .post(baseUrl + "${PhonePageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "1123")
      .check(status.is(303))
      .check(header("Location").saveAs("HaveSecondContactPageUrl"))

  val getHaveSecondContactPage: HttpRequestBuilder =
    http("Get Have-Second-Contact Page")
      .get(baseUrl + "${HaveSecondContactPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postHaveSecondContactPage: HttpRequestBuilder =
    http("Submit Have-Second-Contact (Yes)")
      .post(baseUrl + "${HaveSecondContactPageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").saveAs("SecondContactNamePageUrl"))

  val getSecondContactNamePage: HttpRequestBuilder =
    http("Get 2nd-Contact-Name Page")
      .get(baseUrl + "${SecondContactNamePageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactNamePage: HttpRequestBuilder =
    http("Submit 2nd-Contact-Name")
      .post(baseUrl + "${SecondContactNamePageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "James Bond")
      .check(status.is(303))
      .check(header("Location").saveAs("SecondContactEmailPageUrl"))

  val getSecondContactEmailPage: HttpRequestBuilder =
    http("Get 2nd-Contact-Email Page")
      .get(baseUrl + "${SecondContactEmailPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactEmailPage: HttpRequestBuilder =
    http("Submit 2nd-Contact-Email")
      .post(baseUrl + "${SecondContactEmailPageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "varg.james@gmail.com")
      .check(status.is(303))
      .check(header("Location").saveAs("SecondHavePhonePageUrl"))

  val getSecondContactHavePhonePage: HttpRequestBuilder =
    http("Get Second Contact Have Phone Page")
      .get(baseUrl + "${SecondHavePhonePageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postSecondContactHavePhonePage: HttpRequestBuilder =
    http("Submit Second Contact Have Phone Page")
      .post(baseUrl + "${SecondHavePhonePageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").saveAs("SecondContactPhonePageUrl"))

  val getSecondContactPhonePage: HttpRequestBuilder =
    http("Get Second Contact Phone Page")
      .get(baseUrl + "${SecondContactPhonePageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))


  val postSecondContactPhonePage: HttpRequestBuilder =
    http("Submit Second Contact Phone Page")
      .post(baseUrl + "${SecondContactPhonePageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .formParam("value", "343243")
      .check(status.is(303))
      .check(header("Location").saveAs("CheckAnswersPageUrl"))

  val getCheckAnswersPage: HttpRequestBuilder =
    http("Get Check Answers Page")
      .get(baseUrl + "${CheckAnswersPageUrl}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCheckAnswersPage: HttpRequestBuilder =
    http("Submit Check Answers Page")
      .post(baseUrl + "${CheckAnswersPageUrl}")
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").saveAs("SubmissionConfirmationUrl"))
}
