package com.zarniwoop.xpose.actor

sealed trait ActorMessage

// DNSActor messages
sealed trait DNSMessage extends ActorMessage
case class IPV4ReverseLookup(address: String) extends DNSMessage
case class IPV4Lookup(hostname: String) extends DNSMessage

// PortActor messages
sealed trait TCPMessage extends ActorMessage
case class TCPPortScan(address: String, port: Int, timeout: Int) extends TCPMessage
case class TCPPortScanRange(address: String, portStart: Int, portEnd: Int, timeout: Int) extends TCPMessage

// HostScanActor messages
sealed trait HostScanMessage extends ActorMessage
case class Host(hostname: String) extends HostScanMessage
case class DNSResponse(messages: Array[String]) extends HostScanMessage

sealed trait HTTPMessage extends ActorMessage
case class HTTPSniffRequest(host: String, port: Int = 80, path: String = "/") extends HTTPMessage
case class HTTPSniffResponse(host: String, port: Int) extends HTTPMessage

sealed trait FTPMessage extends ActorMessage
case class FTPSniffRequest(host: String, port: Int = 21) extends FTPMessage
case class FTPSniffResponse(host: String, port: Int) extends FTPMessage