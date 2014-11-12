package pw.anisimov.tinker.web

import java.util.concurrent.TimeUnit

import akka.actor.Props
import akka.testkit.TestProbe
import akka.util.Timeout
import org.specs2.mutable.Specification
import pw.anisimov.tinker.api._
import pw.anisimov.tinker.stub.MockServiceActor
import pw.anisimov.tinker.web.route.GeneralRoute
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest

/**
 * Spray route test
 */
class GeneralRouteTest extends Specification with Specs2RouteTest with HttpService with GeneralRoute {
  def actorRefFactory = system

  val timeout = Timeout(5, TimeUnit.SECONDS)

  def executionContext = actorRefFactory.dispatcher

  val probe = TestProbe()
  system.actorOf(Props(classOf[MockServiceActor], probe.ref), "TinkerModuleService")

  "The service" should {
    "return unhandled on root path" in {
      Get() ~> generalRoute ~> check {
        handled must beFalse
      }
    }

    "handle all options request in sim path" in {
      Options("/node-sim") ~> generalRoute ~> check {
        responseAs[String] === "ok"
      }
    }

    "handle GET requests to status path" in {
      Get("/node-sim/status") ~> generalRoute ~> check {
        responseAs[NodesStatusResponse].nodesStatus.timeout === 10
      }
    }

    "handle POST request to node/add" in {
      Post("/node-sim/node/add", AddNode(10)) ~> generalRoute ~> check {
        responseAs[GeneralResponse] === GeneralResponse(status = true)
      }
    }

    "handle POST request to node/delete" in {
      Post("/node-sim/node/delete", RemoveNode(10)) ~> generalRoute ~> check {
        responseAs[GeneralResponse] === GeneralResponse(status = true)
      }
    }

    "handle POST request to node/timer" in {
      Post("/node-sim/node/timer", ChangeTimeout(20)) ~> generalRoute ~> check {
        responseAs[GeneralResponse] === GeneralResponse(status = true)
      }
    }
  }
}
