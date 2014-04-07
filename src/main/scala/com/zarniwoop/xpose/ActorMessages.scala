package com.zarniwoop.xpose

sealed trait ActorMessage

// DNSActor messages
sealed trait DNSMessage extends ActorMessage
case class IPV4ReverseLookup(address: String) extends DNSMessage
case class IPV4Lookup(hostname: String) extends DNSMessage

// PortActor messages
sealed trait PortMessage extends ActorMessage
case class TCPPortScan(address: String, port: Int, timeout: Int) extends PortMessage
case class TCPPortScanBatch(address: String, portStart: Int, portEnd: Int, timeout: Int) extends PortMessage

// HostScanActor messages
sealed trait HostScanMessage extends ActorMessage
case class Host(hostname: String) extends HostScanMessage
case class DNSResponse(messages: Array[String]) extends HostScanMessage
