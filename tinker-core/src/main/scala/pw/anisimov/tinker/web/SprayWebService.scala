package pw.anisimov.tinker.web

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import pw.anisimov.tinker.web.route.GeneralRoute

/**
 * Copyright (C) 2012-2013 Coldsnipe LLC. <http://www.coldsnipe.com>
 */
class SprayWebService extends Actor with GeneralRoute {
  def actorRefFactory = context

  val timeout = akka.util.Timeout(10, TimeUnit.SECONDS)

  def executionContext = actorRefFactory.dispatcher

  def receive: Actor.Receive = runRoute(generalRoute)
}
