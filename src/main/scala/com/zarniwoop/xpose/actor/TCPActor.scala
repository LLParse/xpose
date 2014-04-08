package com.zarniwoop.xpose.actor

import akka.actor._
import java.net._

class TCPActor extends Actor {

  def receive = {
    case message: TCPMessage => message match {
      case TCPPortSniff(address, port, timeout) => {
        val start = System.currentTimeMillis()

        val s = new Socket()
        var response: String = null
        var connections = 0
        var refusals = 0
        var timeouts = 0
        try {
          s.connect(new InetSocketAddress(address, port), timeout)
          response = "Connected"
          connections += 1
        } catch {
          case e: SocketTimeoutException => {
            response = "Timeout"

            // if TCP connections have been refused in the past, wait for a while
            // and reschedule this work because we may have hit the limiter
            if (refusals > 0 && timeouts == 0) {
              response += " (sleep)"
              Thread.sleep(200)
              self ! message
            } else {
              timeouts += 1
            }
          }
          case e: ConnectException => e.getMessage match {
            case "Connection refused" => {
              response = "Refused"
              refusals += 1
            }
            case _ => {
              response = e.getMessage
            }
          }
        }

        var millis = (System.currentTimeMillis() - start).asInstanceOf[Int]
        sender ! millis
        if (response == "Connected")
          System.out.println("(%s) %s: %s:%d (%dms)".format(self.path.name, response, address, port, millis))
      }
      case TCPPortSniffRange(address, startPort, endPort, timeout) => {
        for (port <- startPort to endPort) {
          self ! TCPPortSniff(address, port, timeout)
        }
      }
    }
  }

}