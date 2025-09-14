import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ApiBaselineSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val scn = scenario("API Baseline Performance Test")
    .exec(
      http("Get Tags")
        .get("/tags")
        .check(status.is(200))
    )
    .pause(100.milliseconds)
    .exec(
      http("Get Articles")
        .get("/articles")
        .check(status.is(200))
    )
    .pause(100.milliseconds)
    .exec(
      http("Get Articles with Limit")
        .get("/articles?limit=10&offset=0")
        .check(status.is(200))
    )
    .pause(100.milliseconds)
    .exec(
      http("Register User")
        .post("/users")
        .body(StringBody("""{"user":{"username":"testuser${__Random(1,10000)}","email":"test${__Random(1,10000)}@example.com","password":"password123"}}"""))
        .check(status.is(200))
        .check(jsonPath("$.user.token").saveAs("authToken"))
    )
    .pause(100.milliseconds)
    .exec(
      http("Login User")
        .post("/users/login")
        .body(StringBody("""{"user":{"email":"test${__Random(1,10000)}@example.com","password":"password123"}}"""))
        .check(status.in(200, 422))
    )
    .pause(100.milliseconds)
    .exec(
      http("Get Profile")
        .get("/profiles/testuser")
        .check(status.in(200, 404))
    )

  setUp(
    scn.inject(
      constantUsersPerSec(10) during (30.seconds),
      rampUsersPerSec(10) to 50 during (60.seconds),
      constantUsersPerSec(50) during (120.seconds)
    )
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.max.lt(5000),
     global.responseTime.mean.lt(1000),
     global.successfulRequests.percent.gt(95)
   )
}
