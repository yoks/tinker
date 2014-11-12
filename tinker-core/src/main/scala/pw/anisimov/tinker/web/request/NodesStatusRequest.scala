package pw.anisimov.tinker.web.request

import akka.actor.{Actor, Props}
import pw.anisimov.tinker.api.{GetNodesStatus, NodesStatus}
import pw.anisimov.tinker.web.NodesStatusResponse

/**
 * Request to get current nodes status
 */
class NodesStatusRequest extends Actor {
  override def preStart(): Unit = {
    context.actorSelection("/user/TinkerModuleService").tell(GetNodesStatus, self)
  }

  override def receive: Receive = {
    case msg: NodesStatus =>
      context.parent ! NodesStatusResponse(msg)
  }
}

object NodesStatusRequest {
  def props(): Props =
    Props(classOf[NodesStatusRequest])
}
