package com.github.blemale.computerdatabase // The optional package.

import io.gatling.core.Predef._
import io.gatling.core.structure.{ScenarioBuilder, ChainBuilder}
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation { // The class declaration. Note that it extends Simulation.

  object Search {
    val search: ChainBuilder = ??? // Extract Search chain, i.e. exec(http(...)).exec(...)...
  }

  object Browse {
    val browse: ChainBuilder = ??? // Extract Browse chain
  }

  object Edit {
    val edit: ChainBuilder = ??? // Extract Edit chain
  }

  val httpProtocol = http
    .baseURL("http://computer-database.gatling.io")
    .inferHtmlResources(BlackList( """.*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())

  val uri1 = "http://computer-database.gatling.io"

  val scn = scenario("BasicSimulation") // The scenario definition.
		// Search
		.exec(http("request_0") // A HTTP request, named request_0. This name will be displayed in the final reports.
			.get("/")) // The url this request targets with the GET method.
		.pause(2) // Some pause/think time.
		.exec(http("request_1")
			.get("/computers?f=macbook"))
		.pause(4)
		.exec(http("request_2")
			.get("/computers/516"))
		.pause(3)
		// Browse
		.exec(http("request_3")
			.get("/"))
		.pause(2)
		.exec(http("request_4")
			.get("/computers?p=1"))
		.pause(2)
		.exec(http("request_5")
			.get("/computers?p=2"))
		.pause(2)
		.exec(http("request_6")
			.get("/computers?p=3"))
		.pause(2)
		// Edit
		.exec(http("request_7")
			.get("/computers/new"))
    .pause(6)
    .exec(http("request_8")
      .post("/computers")
      .formParam("name", "Amiga 2000")
      .formParam("introduced", "1987-03-01")
      .formParam("discontinued", "1991-01-01")
      .formParam("company", "6"))

  val scnWithPageObjects: ScenarioBuilder = ??? // Rewrite scenario using these reusable business processes

  setUp(scnWithPageObjects.inject(atOnceUsers(1))).protocols(httpProtocol)
}