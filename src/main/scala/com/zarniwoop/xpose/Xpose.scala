package com.zarniwoop.xpose

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.routing.SmallestMailboxRouter
import akka.actor.Props

object Xpose extends App {
  val system = ActorSystem("xpose")
  val scan = system.actorOf(Props[HostScanActor], "dns")
  scan ! Host("www.pegs.com")
  scan ! Host("www.google.com")
  scan ! Host("www.gmail.com")
  scan ! Host("www.facebook.com")
}