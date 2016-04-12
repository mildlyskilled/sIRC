package com.mildlyskilled.actors

import akka.testkit.TestFSMRef
import com.mildlyskilled.protocol.Channel._
import com.mildlyskilled.protocol.Message._


class ChannelSpec extends ActorHarness{
  "A Channel" must {

    val channelActor = TestFSMRef(new Channel, "TestChannel")

    "be Idle when initialised" in {
      channelActor.underlyingActor.stateName must be(Idle)
    }

    "transition to active when sent a Start message" in {
      channelActor ! Start
      channelActor.underlyingActor.stateName must be(Active)
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

    "remove a user when it receives RemoveUser message" in {
      channelActor ! RemoveUser(testActor)
      channelActor.underlyingActor.stateData match {
        case ChannelState(participants) => assert(!participants.contains(testActor))
        case _ => fail("There needs to be a channel state")
      }
    }
  }
}
