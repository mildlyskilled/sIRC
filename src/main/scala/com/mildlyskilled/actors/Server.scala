package com.mildlyskilled.actors

import akka.actor.FSM
import com.mildlyskilled.protocol.Server._

class Server extends FSM[State, Data] {

  startWith(Idle, Channels(Nil))

  when(Idle) {
    case Event(Start, _) =>
      goto(Active) using Channels(Nil)
  }

  when(Active) {
    case Event(Start, _) =>
      stay

    case Event(Register(r), serverChannels@Channels(c)) =>
      stay using serverChannels.copy(channels = r::c)
  }

  initialize()

}
