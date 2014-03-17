package com.zarniwoop.xpose;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class AkkaTest {

	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef myActor = system.actorOf(Props.create(A.class), "WTF");
		
	}
	
	class A extends UntypedActor {

		@Override
		public void onReceive(Object message) throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
}
