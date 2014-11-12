package pw.anisimov.tinker.web.request

import akka.actor.{Actor, Props}
import pw.anisimov.tinker.api.AddNode
import pw.anisimov.tinker.web.GeneralResponse

/**
 * Request to add nodes to Sim
 * @param count nodes to add
 */
class AddNodesRequest(count: Int) extends Actor {
  override def preStart(): Unit = {
    context.actorSelection("/user/TinkerModuleService").tell(AddNode(count), self)
    context.parent ! GeneralResponse(status = true)
  }

  override def receive: Receive = {
    case msg =>
  }
}

object AddNodesRequest {
  def props(count: Int): Props =
    Props(classOf[AddNodesRequest], count)
}
