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
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object Requests extends ServicesConfiguration {

  val baseUrl: String     = baseUrlFor("crs-fatca-fi-management-frontend")
  val baseUrlAuth: String = baseUrlFor("auth-frontend")
  val route: String       = "/manage-your-crs-and-fatca-financial-institutions"
  val authRoute: String   = "/auth-login-stub/gg-sign-in"
  val amazonUrlPattern    = """action="(.*?)""""

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

  val postCRSFATCADashboardPage: HttpRequestBuilder =
    http("Get CRS FATCA Dashboard Page")
      .post("${LandingPage}/add")
      .check(status.is(403))


}
