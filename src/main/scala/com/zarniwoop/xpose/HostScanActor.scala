package com.zarniwoop.xpose

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout
import akka.routing.RoundRobinRouter

class HostScanActor extends Actor {

  val dns = context.actorOf(Props[DNSActor], "dns")
  val scan = context.actorOf(Props[PortActor].withRouter(RoundRobinRouter(5)), "port")

  def receive = {
    case message: HostScanMessage => message match {
      case Host(hostname) => {
        dns ! IPV4Lookup(hostname)
      }
      case DNSResponse(addresses) => {
        for (address <- addresses) {

          // First try scanning port 80 for response time profiling
          implicit val timeout = Timeout(5 seconds)
          val future = scan ? TCPPortScan(address, 80, 3000)
          val duration = Await.result(future, timeout.duration).asInstanceOf[Int] * 2

          scan ! TCPPortScanBatch(address, 1, 65535, duration)
        }
      }
    }
  }
}