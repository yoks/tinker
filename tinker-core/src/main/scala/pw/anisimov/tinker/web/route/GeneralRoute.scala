package pw.anisimov.tinker.web.route

import akka.util.Timeout
import pw.anisimov.tinker.api.{AddNode, ChangeTimeout, RemoveNode}
import pw.anisimov.tinker.web.JsonProtocol
import pw.anisimov.tinker.web.request._
import spray.http.AllOrigins
import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import spray.http.HttpMethods._
import spray.routing.HttpService

import scala.concurrent.ExecutionContext

trait GeneralRoute extends HttpService with JsonProtocol with PerRequestCreator {
  implicit val timeout: Timeout

  implicit def executionContext: ExecutionContext

  val corsHeaders = List(`Access-Control-Allow-Origin`(AllOrigins),
    `Access-Control-Allow-Methods`(GET, POST, OPTIONS, DELETE),
    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent"))

  val generalRoute = {
    respondWithHeaders(corsHeaders) {
      pathPrefix("node-sim") {
        options {
          complete {
            "ok"
          }
        } ~
          path("status") {
            get { ctx =>
              perRequest(ctx, NodesStatusRequest.props())
            }
          } ~
          path("node" / "delete") {
            post {
              decompressRequest() {
                entity(as[RemoveNode]) {
                  removeNode => { ctx =>
                    perRequest(ctx, DeleteNodesRequest.props(removeNode.nodes))
                  }
                }
              }
            }
          } ~
          path("node" / "add") {
            post {
              decompressRequest() {
                entity(as[AddNode]) {
                  addNode => { ctx =>
                    perRequest(ctx, AddNodesRequest.props(addNode.nodes))
                  }
                }
              }
            }
          } ~
          path("node" / "timer") {
            post {
              decompressRequest() {
                entity(as[ChangeTimeout]) {
                  changeTimeout => { ctx =>
                    perRequest(ctx, ChangeTimerRequest.props(changeTimeout.timeout))
                  }
                }
              }
            }
          }
      }
    }
  }
}
