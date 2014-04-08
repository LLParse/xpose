package com.zarniwoop.xpose

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.routing.SmallestMailboxRouter
import akka.actor.Props
import com.zarniwoop.xpose.actor.HostScanActor
import com.zarniwoop.xpose.actor.Host
import com.zarniwoop.xpose.actor.HTTPActor
import com.zarniwoop.xpose.actor.HTTPSniffRequest
import com.zarniwoop.xpose.actor.FTPActor
import com.zarniwoop.xpose.actor.FTPSniffRequest

object Xpose extends App with Logging {
  info("Starting actor system")
  val system = ActorSystem("xpose")
  //val scan = system.actorOf(Props[HostScanActor], "dns")
  //scan ! Host("www.pegs.com")
  //scan ! Host("www.google.com")
  //scan ! Host("www.gmail.com")
  //scan ! Host("www.facebook.com")

  val http = system.actorOf(Props[HTTPActor], "http")
  http ! HTTPSniffRequest("google.com")

  val ftp = system.actorOf(Props[FTPActor], "ftp")
  ftp ! FTPSniffRequest("ftp.hq.nasa.gov")
}