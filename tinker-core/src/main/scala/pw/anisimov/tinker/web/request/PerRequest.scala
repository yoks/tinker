package pw.anisimov.tinker.web.request

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import pw.anisimov.tinker.web.request.PerRequest.WithProps
import pw.anisimov.tinker.web.{GeneralResponse, JsonProtocol, NodesStatusResponse, RestResponse}
import spray.http.StatusCodes._
import spray.http._
import spray.httpx.marshalling.BasicMarshallers
import spray.routing.RequestContext

import scala.concurrent.duration._

trait PerRequest extends Actor with ActorLogging with JsonProtocol with BasicMarshallers {

  import context._

  def r: RequestContext

  setReceiveTimeout(9.seconds)

  def receive = {
    case res: RestResponse => complete(OK, res)
    case ReceiveTimeout =>
      complete(GatewayTimeout, GeneralResponse(status = false))
  }

  def complete[T <: RestResponse](status: StatusCode, obj: T) = {
    obj match {
      case msg: GeneralResponse =>
        r.complete(status, msg)
      case msg: NodesStatusResponse =>
        r.complete(status, msg)
    }
    stop(self)
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case e =>
        complete(InternalServerError, GeneralResponse(status = false))
        Stop
    }
}

object PerRequest {

  case class WithProps(r: RequestContext, props: Props) extends PerRequest {
    context.actorOf(props)
  }

}

trait PerRequestCreator {
  implicit def actorRefFactory: ActorRefFactory

  def perRequest(r: RequestContext, props: Props) =
    actorRefFactory.actorOf(Props(new WithProps(r, props)))
}
