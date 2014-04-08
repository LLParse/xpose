package com.zarniwoop.xpose.actor

import akka.actor.Actor
import java.net.Socket
import java.io.BufferedReader
import java.io.PrintWriter
import java.io.InputStreamReader
import com.zarniwoop.xpose.Logging

class HTTPActor extends Actor with TCPStream with Logging {

  def receive = {
    case HTTPSniffRequest(host, port, path) => {
      if (socket(host, port)) {
        var isHTTP: Boolean = true
        write(getGETRequest(host, path))
        read() match {
          case x if x.startsWith("HTTP/1.") => {
            val code = x.substring(0, x.indexOf("\r\n")).split(" +")(1)
            val desc = HTTPStatusCodes1_1.codeMap.get(code.toInt)
            debug("HTTP: (" + code + ") " + desc.getOrElse("Unknown"))
          }
          case _ => isHTTP = false
        }
        close()
        if (isHTTP)
          sender ! HTTPSniffResponse(host, port)
      }
    }
  }

  private def getGETRequest(host: String, path: String): String = {
    "GET " + path + " HTTP/1.1\r\n" + "Host: " + host + "\r\n\r\n"
  }
}

object HTTPStatusCodes1_1 {
  def codeMap = Map(
    // Informational 1xx
    100 -> "Continue",
    101 -> "Switching Protocols",
    // Successful 2xx
    200 -> "OK",
    201 -> "Created",
    202 -> "Accepted",
    203 -> "Non-Authoritative Information",
    204 -> "No Content",
    205 -> "Reset Content",
    206 -> "Partial Content",
    // Redirection 3xx
    300 -> "Multiple Choices",
    301 -> "Moved Permanently",
    302 -> "Found",
    303 -> "See Other",
    304 -> "Not Modified",
    305 -> "Use Proxy",
    307 -> "Temporary Redirect",
    // Client Error 4xx
    400 -> "Bad Request",
    401 -> "Unauthorized",
    402 -> "Payment Required",
    403 -> "Forbidden",
    404 -> "Not Found",
    405 -> "Method Not Allowed",
    406 -> "Not Acceptable",
    407 -> "Proxy Authentication Required",
    408 -> "Request Timeout",
    409 -> "Conflict",
    410 -> "Gone",
    411 -> "Length Required",
    412 -> "Precondition Failed",
    413 -> "Request Entity Too Large",
    414 -> "Request-URI Too Long",
    415 -> "Unsupported Media Type",
    416 -> "Requred Range Not Satisfiable",
    417 -> "Expectation Failed",
    // Server Error 5xx
    500 -> "Internal Server Error",
    501 -> "Not Implemented",
    502 -> "Bad Gateway",
    503 -> "Service Unavailable",
    504 -> "Gateway Timeout",
    505 -> "HTTP Version Not Supported")
}