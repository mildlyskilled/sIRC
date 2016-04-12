package com.mildlyskilled.actors

import akka.actor.FSM
import com.mildlyskilled.protocol.Channel._
import com.mildlyskilled.protocol.Message._

class Channel extends FSM[State, Data] {

  startWith(Idle, ChannelState(Nil))

  when(Idle) {
    case Event(RegisterUser(p), s@ChannelState(participants)) =>
      sender ! Info(s"${p.path.name} registered with ${self.path.name}")
      p ! Info(s"${p.path.name} registered with ${self.path.name}")
      goto(Active) using s.copy(participants = p :: participants)
    case Event(Start, _) =>
      goto(Active)
  }

  when(Active) {
    case Event(RegisterUser(p), s@ChannelState(participants)) =>
      sender ! Info(s"${p.path.name} registered with ${self.path.name}")
      p ! Info(s"${p.path.name} registered with ${self.path.name}")
      stay using s.copy(participants = p :: participants)

    case Event(RegisteredUsers, s@ChannelState(participants)) =>
      sender ! RegisteredUsers(participants.map(a => a.path.name))
      log.info(s"Sending list to ${sender.path.name}")
      stay using s

    case Event(RemoveUser(user), s@ChannelState(participants)) =>
      user ! Info(s"Removing ${user.path.name} from ${self.path.name}")
      stay using s.copy(participants = participants.filterNot((x) => x == user))
  }

  onTransition {
    case _ -> Active =>
      log.info(Console.BLUE_B + "Channel going ACTIVE" + Console.RESET)
    case _ -> Idle =>
      log.info(Console.BLUE_B + "Channel going IDLE" + Console.RESET)
  }
}
