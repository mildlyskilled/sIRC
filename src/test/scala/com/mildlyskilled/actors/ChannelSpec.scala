package com.mildlyskilled.actors

import akka.testkit.TestFSMRef
import com.mildlyskilled.protocol.Channel._


class ChannelSpec extends ActorHarness{
  "A Channel" must {

    val channelActor = TestFSMRef(new Channel, "TestChannel")

    "be Idle when initialised" in {
      channelActor.underlyingActor.stateName must be(Idle)
    }

    "allow users to register" in {
      channelActor ! RegisterUser(testActor)
      expectMsg(Info(s"${testActor.path.name} registered with ${channelActor.path.name}"))
    }

    "transition to active when first registration is received" in {
      channelActor.underlyingActor.stateName must be(Active)
    }

    "return list of registered users on demand" in {
      channelActor ! RegisteredUsers
      expectMsg(Info(s"${testActor.path.name} registered with ${channelActor.path.name}"))
      expectMsg(RegisteredUsers(List(testActor.path.name)))
    }
  }
}
