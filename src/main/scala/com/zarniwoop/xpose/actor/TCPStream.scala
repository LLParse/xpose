package com.zarniwoop.xpose.actor

import java.net.Socket
import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import com.zarniwoop.xpose.Logging

trait TCPStream extends Logging {
  private var sock: Socket = null
  private var writer: PrintWriter = null
  private var reader: BufferedReader = null

  def socket(host: String, port: Int) = {
    sock = new Socket(host, port)
    debug("Opened socket to " + host + ":" + port)
    writer = new PrintWriter(sock.getOutputStream())
    reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
  }

  def write(data: String) = {
    writer.print(data)
    writer.flush()
  }

  def read(): String = {
    Stream.continually(reader.read().asInstanceOf[Char]).takeWhile(_ != -1 && reader.ready()).mkString
  }
  
  def close() = {
    sock.close()
    debug("Closed socket")
  }
}