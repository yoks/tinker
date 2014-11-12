package pw.anisimov.tinker.web.request

import akka.actor.{Actor, Props}
import pw.anisimov.tinker.api.RemoveNode
import pw.anisimov.tinker.web.GeneralResponse

/**
 * Request to delete nodes from Sim
 * @param count nodes to delete
 */
class DeleteNodesRequest(count: Int) extends Actor {
  override def preStart(): Unit = {
    context.actorSelection("/user/TinkerModuleService").tell(RemoveNode(count), self)
    context.parent ! GeneralResponse(status = true)
  }

  override def receive: Receive = {
    case msg =>
  }

}

object DeleteNodesRequest {
  def props(count: Int): Props =
    Props(classOf[DeleteNodesRequest], count)
}
