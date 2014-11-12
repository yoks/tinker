package pw.anisimov.tinker

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import pw.anisimov.tinker.api._

/**
 * Test service actor
 */
class TinkerModuleServiceTest extends TestKit(ActorSystem("tinker-sim-test")) with FlatSpecLike with BeforeAndAfterAll
with ImplicitSender with Matchers {
  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Tinker Module Service" should "correctly initialize nodes" in {
    val service = system.actorOf(Props[TinkerModuleService])
    watch(service)
    expectNoMsg()
    service ! GetNodesStatus
    expectMsgClass(classOf[NodesStatus]).nodeStatus.size should be(3)
    service ! PoisonPill
    expectTerminated(service)
  }

  it should "correctly delete nodes" in {
    val service = system.actorOf(Props[TinkerModuleService])
    watch(service)
    expectNoMsg()
    service ! GetNodesStatus
    expectMsgClass(classOf[NodesStatus]).nodeStatus.size should be(3)
    service ! RemoveNode(2)
    expectNoMsg()
    service ! GetNodesStatus
    expectMsgClass(classOf[NodesStatus]).nodeStatus.size should be(1)
  }

  it should "correctly add nodes" in {
    val service = system.actorOf(Props[TinkerModuleService])
    watch(service)
    expectNoMsg()
    service ! GetNodesStatus
    expectMsgClass(classOf[NodesStatus]).nodeStatus.size should be(3)
    service ! AddNode(2)
    expectNoMsg()
    service ! GetNodesStatus
    expectMsgClass(classOf[NodesStatus]).nodeStatus.size should be(5)
  }

  it should "correctly change timeout of nodes" in {
    val service = system.actorOf(Props[TinkerModuleService])
    watch(service)
    expectNoMsg()
    service ! GetNodesStatus
    expectMsgClass(classOf[NodesStatus]).timeout should be(10)
    service ! ChangeTimeout(20)
    expectNoMsg()
    service ! GetNodesStatus
    expectMsgClass(classOf[NodesStatus]).timeout should be(20)
  }
}
