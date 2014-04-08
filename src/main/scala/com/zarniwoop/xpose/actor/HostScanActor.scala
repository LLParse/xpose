package com.zarniwoop.xpose.actor

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.util.Timeout
import akka.routing.RoundRobinRouter
import java.util.concurrent.TimeoutException
import akka.pattern.ask

class HostScanActor extends Actor {

  val dns = context.actorOf(Props[DNSActor], "dns")
  val scan = context.actorOf(Props[TCPActor].withRouter(RoundRobinRouter(5)), "port")

  def receive = {
    case message: HostScanMessage => message match {
      case Host(hostname) => {
        dns ! IPV4Lookup(hostname)
      }
      case DNSResponse(addresses) => {
        for (address <- addresses) {

          // First try scanning port 80 for response time profiling
          implicit val timeout = Timeout(5 seconds)
          var duration = 3000
          val future = scan ? TCPPortSniff(address, 80, duration)
          try {
            duration = Await.result(future, timeout.duration).asInstanceOf[Int] * 2
          } catch {
            case e: TimeoutException => Nil 
          }

          // Break up scans into chunk
          val step = 1024
          for (i <- 1 to 65536 by step)
            scan ! TCPPortSniffRange(address, i, i + step - 1, duration)
        }
      }
    }
  }
}