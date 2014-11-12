package pw.anisimov.tinker.kernel

import akka.actor.{ActorSystem, Props}
import akka.kernel.Bootable
import pw.anisimov.tinker.TinkerModuleService

class TinkerModuleKernel extends Bootable {
  val system = ActorSystem("tinker-sim")

  def startup(): Unit = {
    system.actorOf(Props(classOf[TinkerModuleService]), "TinkerModuleService")
  }

  def shutdown(): Unit = {
    system.shutdown()
  }
}
