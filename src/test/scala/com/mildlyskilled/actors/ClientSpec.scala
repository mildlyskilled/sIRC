package com.mildlyskilled.actors

import akka.testkit.TestFSMRef
import com.mildlyskilled.protocol.Client._
import com.mildlyskilled.protocol.Message._


class ClientSpec extends ActorHarness {

  "A Client" must {
    val clientActor = TestFSMRef(new Client, "client")
    val serverActor = TestFSMRef(new Server, "client_server")
    serverActor ! Start

    "have a name" in {
        clientActor.underlyingActor.stateData match {
          case t: ClientData => t.name must be("client")
          case _ => fail("Client must have client data")
        }
    }

    "be Idle when started" in {
      clientActor.underlyingActor.stateName must be(Idle)
    }

    "transition to Active when connected to a server" in {
      serverActor ! Start
      clientActor ! Connect(serverActor)
      clientActor.underlyingActor.stateData match {
        case cd: ClientData => cd.server must be(Some(serverActor))
        case _ => fail("There must be client data ")
      }
    }
  }
}
