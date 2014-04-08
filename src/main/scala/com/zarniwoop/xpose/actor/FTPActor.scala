package com.zarniwoop.xpose.actor

import akka.actor.Actor
import com.zarniwoop.xpose.Logging

class FTPActor extends Actor with TCPStream with Logging {

  def receive = {
    case FTPSniffRequest(host, port) => {
      var isFTP: Boolean = true
      socket(host, port)
      read() match {
        case x if x.startsWith("220") => debug("FTP: Hello")
        case _ => isFTP = false
      }
      if (isFTP) {
        read()
        write("QUIT\r\n")
        read() match {
          case x if x.startsWith("221") => debug("FTP: Goodbye")
          case _ => isFTP = false
        }
      }
      close()
      if (isFTP)
        sender ! FTPSniffResponse(host, port)
    }
  }

}