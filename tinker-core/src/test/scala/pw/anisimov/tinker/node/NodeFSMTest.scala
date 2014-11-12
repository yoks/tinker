package pw.anisimov.tinker.node

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import pw.anisimov.tinker.api.{ChangeNodeState, NodeMessage, NodeStatus, ShutdownNode}

/**
 * Test for FSM Node logic
 */
class NodeFSMTest extends TestKit(ActorSystem("tinker-sim-test")) with FlatSpecLike with BeforeAndAfterAll
with ImplicitSender with Matchers {
  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Node FSM" should "correctly treat messages" in {
    val node = system.actorOf(Props(classOf[Node], self))
    expectNoMsg()
    node ! ChangeNodeState(1500L, Vector(self))
    expectMsgClass(classOf[NodeStatus])
    expectMsg(NodeMessage)
    watch(node)
    node ! ShutdownNode
    expectTerminated(node)
  }
}
