package pw.anisimov.tinker.web.request

import akka.actor.{Actor, Props}
import pw.anisimov.tinker.api.ChangeTimeout
import pw.anisimov.tinker.web.GeneralResponse

/**
 * Request to change nodes timeout timer
 * @param timer new timer value
 */
class ChangeTimerRequest(timer: Long) extends Actor {
  override def preStart(): Unit = {
    context.actorSelection("/user/TinkerModuleService").tell(ChangeTimeout(timer), self)
    context.parent ! GeneralResponse(status = true)
  }

  override def receive: Receive = {
    case msg =>
  }

}

object ChangeTimerRequest {
  def props(timer: Long): Props =
    Props(classOf[ChangeTimerRequest], timer)
}
