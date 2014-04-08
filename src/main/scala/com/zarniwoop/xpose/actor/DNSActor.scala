package com.zarniwoop.xpose.actor

import org.xbill.DNS._
import akka.actor.Actor
import akka.actor.actorRef2Scala

class DNSActor extends Actor {

  val resolver = new SimpleResolver

  def receive = {
    case message: DNSMessage => message match {
      case IPV4Lookup(hostname) => {
        val response = resolve(Name.fromString(hostname, Name.root), Type.A, DClass.IN)
        val answers = response.getSectionArray(Section.ANSWER)
          .iterator
          .filter(_.getType() == Type.A)
          .map(_.rdataToString())
          .toArray

        context.parent ! DNSResponse(answers)
      }
      case IPV4ReverseLookup(address) => {
        resolve(ReverseMap.fromAddress(address, Address.IPv4), Type.A, DClass.IN)
      }
    }
  }

  def resolve(name: Name, dtype: Int, dclass: Int): Message = {
    val record = Record.newRecord(name, dtype, dclass)
    val query = Message.newQuery(record)
    resolver.send(query)
  }

}