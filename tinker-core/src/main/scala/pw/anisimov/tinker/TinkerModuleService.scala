package pw.anisimov.tinker

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.io
import com.typesafe.config.ConfigFactory
import pw.anisimov.tinker.api._
import pw.anisimov.tinker.node.Node
import pw.anisimov.tinker.web.SprayWebService
import spray.can.Http

import scala.annotation.tailrec
import scala.collection.parallel.mutable


/**
 * Parent container for all Simulation Nodes instances
 *
 * Service also route service messages e.g. Node status, cluster health.
 */
class TinkerModuleService extends Actor with ActorLogging {
  // Mutable container for node refs
  val nodesRefs = mutable.ParHashMap[ActorRef, ActorSystem]()

  val config = ConfigFactory.load("node").getConfig("node")
  val initNodes = config.getInt("initial-limit")
  val initialTimeout = config.getDuration("initial-timeout", TimeUnit.MILLISECONDS)

  // Current timeout for all nodes
  var timeout = initialTimeout

  // Stateful log for node messages, contain only latest pull
  val nodeLog = mutable.ParHashMap[String, NodeStatus]()

  override def preStart(): Unit = {
    log.info("Service Node Simulator initializing, Initial nodes count: {}, initial nodes timeout: {}", initNodes, timeout)
    initNode(initNodes)

    val endpoinConfig = ConfigFactory.load("endpoint").getConfig("endpoint")
    val port = endpoinConfig.getInt("server.port")
    val host = endpoinConfig.getString("server.host")
    val webServiceActor = context.actorOf(Props[SprayWebService], "SprayWebService")
    io.IO(Http)(context.system) ! Http.Bind(webServiceActor, host, port = port)
  }

  override def postStop(): Unit = {
    for (node <- nodesRefs.valuesIterator) {
      node.shutdown()
    }
    log.info("Service actor terminated")
  }

  def receive: Actor.Receive = {
    case msg: AddNode =>
      initNode(msg.nodes)
    case msg: RemoveNode =>
      for (i <- 0 until msg.nodes) {
        removeNode()
      }
    case msg: ChangeTimeout =>
      if (msg.timeout > 0) {
        timeout = msg.timeout
      }

      changeState()
    case GetNodesStatus =>
      sender() ! NodesStatus(nodeLog.values.to[List], timeout)
    case msg: Terminated =>
      removeNode(Some(msg.getActor))
    case msg: NodeStatus =>
      nodeLog.put(msg.name, msg)
  }

  def initNode(count: Int): Unit = {
    for (i <- 0 until count) {
      val nodeSystem = ActorSystem("node-system")
      val nodeRef = nodeSystem.actorOf(Props(classOf[Node], self))
      nodesRefs.put(nodeRef, nodeSystem)
      context.watch(nodeRef)
    }

    changeState()
  }

  def changeState(): Unit = {
    for (node <- nodesRefs.keysIterator) {
      node ! ChangeNodeState(timeout, nodesRefs.keySet.to[List])
    }
  }

  @tailrec
  private def removeNode(actor: Option[ActorRef] = None): Unit = {
    actor match {
      case Some(actorRef) =>
        nodesRefs.get(actorRef) match {
          case Some(system) =>
            nodesRefs.remove(actorRef)
            system.shutdown()
          case None =>
            log.warning("Unexpected actor termination {}", actorRef)
        }
        nodeLog.remove(actorRef.toString())
      case None =>
        if (nodesRefs.size > 0) {
          removeNode(Some(nodesRefs.head._1))
        } else {
          log.warning("No more nodes to remove")
        }
    }
  }
}