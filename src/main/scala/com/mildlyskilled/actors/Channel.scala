package com.mildlyskilled.actors

import akka.actor.FSM
import com.mildlyskilled.protocol.Channel._

class Channel extends FSM[State, Data] {

  startWith(Idle, ChannelState(Nil))

  when(Idle) {
    case Event(RegisterUser(p), s@ChannelState(participants)) =>
      sender ! Info(s"${p.path.name} registered with ${self.path.name}")
      p ! Info(s"${p.path.name} registered with ${self.path.name}")
      goto(Active) using s.copy(participants = p::participants)
  }

  when(Active) {
    case Event(RegisterUser(p), s@ChannelState(participants)) =>
      sender ! Info(s"${p.path.name} registered with ${self.path.name}")
      p ! Info(s"${p.path.name} registered with ${self.path.name}")
      stay using s.copy(participants = p::participants)

    case Event(RegisteredUsers, s@ChannelState(participants)) =>
      sender ! RegisteredUsers(participants.map(a => a.path.name))
      log.info(s"Sending list to ${sender.path.name}")
      stay using s
  }
}
