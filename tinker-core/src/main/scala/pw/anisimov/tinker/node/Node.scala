package pw.anisimov.tinker.node

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, FSM}
import pw.anisimov.tinker.api.{ChangeNodeState, NodeMessage, NodeStatus, ShutdownNode}
import pw.anisimov.tinker.node.Node._

import scala.concurrent.duration.Duration


object Node {

  /**
   * States Node can be in
   *
   * Active state means ring in synchronized and can send messages
   */
  sealed trait State

  case object Active extends State

  case object Idle extends State


  /**
   * Container to keep Node Data
   */
  sealed trait Data

  case object Uninitialized extends Data

  case class NodeData(nodes: Seq[ActorRef], messageCount: Long) extends Data

  /**
   * Internal message indicating node need send messages to another nodes
   */
  case object SendMessage

  case object MessageCount

}

class Node(serviceActor: ActorRef) extends Actor with FSM[State, Data] {
  startWith(Idle, Uninitialized)

  when(Idle)(FSM.NullFunction)

  when(Active) {
    case Event(NodeMessage, data: NodeData) =>
      stay() using data.copy(messageCount = data.messageCount + 1)
    case Event(MessageCount, data: NodeData) =>
      log.info("Node: {}, Messages per second: {}", self.toString(), data.messageCount)
      serviceActor ! NodeStatus(data.messageCount, self.toString())
      stay() using data.copy(messageCount = 0)
    case Event(SendMessage, NodeData(nodes, _)) =>
      for (node <- nodes) {
        node ! NodeMessage
      }
      stay()
  }

  whenUnhandled {
    case Event(ChangeNodeState(timeout, nodes), _) =>
      setTimer("node-repeat", SendMessage, Duration(timeout, TimeUnit.MILLISECONDS), repeat = true)
      setTimer("node-message-count", MessageCount, Duration(1, TimeUnit.SECONDS), repeat = true)
      goto(Active) using NodeData(nodes.filter(_ != self), 0)

    case Event(ShutdownNode, _) =>
      cancelTimer("node-repeat")
      cancelTimer("node-message-count")
      stop(FSM.Shutdown)

    case Event(e, s) =>
      log.warning("Node received unhandled request {} in state {}/{}", e, stateName, s)
      stay()
  }

  onTransition {
    case Active -> Idle =>
      cancelTimer("node-repeat")
      cancelTimer("node-message-count")
  }

  initialize()
}
