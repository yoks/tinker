package pw.anisimov.tinker.stub

import akka.actor.{Actor, ActorRef}
import pw.anisimov.tinker.api.{GetNodesStatus, NodesStatus}

class MockServiceActor(probe: ActorRef) extends Actor {
  def receive: Actor.Receive = {
    case GetNodesStatus =>
      sender() ! NodesStatus(List(), 10)
  }
}

class Forwarder(forwardee: ActorRef) extends Actor {
  def receive: Actor.Receive = {
    case msg =>
      forwardee.forward(msg)
  }
}

