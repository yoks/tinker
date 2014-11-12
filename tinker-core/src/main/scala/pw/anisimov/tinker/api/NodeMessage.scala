package pw.anisimov.tinker.api

import akka.actor.ActorRef

/**
 * Dummy node message object
 */
object NodeMessage

/**
 * Shutdown node message
 */
object ShutdownNode

/**
 * Message indicating node settings changed to new state
 * @param timeout timeout between messages
 * @param nodes collection containing addresses of all nodes in ring
 */
case class ChangeNodeState(timeout: Long, nodes: Seq[ActorRef])