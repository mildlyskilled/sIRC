package com.mildlyskilled.actors

import akka.testkit.TestFSMRef
import com.mildlyskilled.protocol.Server._

class ServerSpec extends ActorHarness {

  "The Server" must {

    val server = TestFSMRef(new Server, "server")

    "be idle when instantiated" in {
      server.underlyingActor.stateName must be(Idle)
    }

    "transition to active when it receives a Start message" in {
      server ! Start
      server.underlyingActor.stateName must be(Active)
      server.underlyingActor.stateData match {
        case Channels(x) => x.size must be(1)
        case _ => fail("State Data must exist")
      }
    }

    "allow new channels to be created" in {
      server ! RegisterChannel("channel1")
      server.underlyingActor.stateData match {
        case Channels(channelList) => channelList.map(a => a.path.name).contains("channel1") must be (true)
        case _ => fail("State data must have channels")
      }
    }


    "have a global channel" in {
      server.underlyingActor.stateData match {
        case Channels(ch) => ch.map(c => c.path.name).contains("global")
        case _ => fail("Server must have channel data")
      }
    }

    "allow logins from connecting clients" in {
      server ! Login("test", "test")
      expectMsg(AuthenticationStatus("successful"))
    }

    "put logged in users in the global channel" in {
      expectMsg(Info(s"${testActor.path.name} registered with global"))
    }

    "prevent users from joining channels " in {
      server.tell(JoinChannel("channel2"), testActor)
      expectMsg(Warn("channel2 is not a valid channel"))
    }

    "allow users to join registered channels " in {
      server.tell(JoinChannel("channel1"), testActor)
      expectMsg(Info(s"${testActor.path.name} registered with channel1"))
    }
  }
}
