package com.github.blemale.computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  object Search {
    val feeder = csv("search.csv").random

    val search =
      exec(http("Home")
        .get("/"))
        .pause(2)
        .feed(feeder)
        .exec(http("Search")
          .get("/computers?f=${searchCriterion}")
          .check(css("a:contains('${searchComputerName}')", "href").saveAs("computerURL")))
        .pause(4)
        .exec(http("Select")
          .get("${computerURL}"))
        .pause(3)
  }

  object Browse {
    val browse =
      exec(http("Home")
        .get("/"))
        .pause(2)
        .exec(http("Page 1")
          .get("/computers?p=1"))
        .pause(2)
        .exec(http("Page 2")
          .get("/computers?p=2"))
        .pause(2)
        .exec(http("Page 3")
          .get("/computers?p=3"))
        .pause(2)
  }

  object Edit {
    val edit =
      exec(http("Edit")
        .get("/computers/new"))
        .pause(6)
        .exec(http("Add")
          .post("/computers")
          .formParam("name", "Amiga 2000")
          .formParam("introduced", "1987-03-01")
          .formParam("discontinued", "1991-01-01")
          .formParam("company", "6"))
  }

  val httpProtocol = http
    .baseURL("http://computer-database.gatling.io")
    .inferHtmlResources(BlackList( """.*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())

  val uri1 = "http://computer-database.gatling.io"

  val users = scenario("Users").exec(Search.search, Browse.browse)
  val admins = scenario("Admins").exec(Search.search, Browse.browse, Edit.edit)

  setUp(
    users.inject(rampUsers(10) over (10 seconds)),
    admins.inject(rampUsers(2) over (10 seconds))
  ).protocols(httpProtocol)
}