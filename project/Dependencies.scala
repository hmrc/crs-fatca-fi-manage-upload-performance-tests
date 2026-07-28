import sbt.*

object Dependencies {

  val test = Seq(
    "uk.gov.hmrc" %% "performance-test-runner" % "6.3.0" % Test,
    "com.typesafe" % "config"                  % "1.4.9" % Test
  )

}
