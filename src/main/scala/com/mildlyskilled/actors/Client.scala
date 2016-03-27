package com.mildlyskilled.actors

import akka.actor.FSM
import com.mildlyskilled.protocol.Client._


class Client extends FSM[State, Data] {

  startWith(Idle, ClientData(self.path.name, None, Nil))

  when(Idle) {
    case Event(Connect(serverRef), cd: ClientData) =>
      log.info(Console.GREEN + s"Sent register message to server at ${serverRef.path.name}" + Console.RESET)
      goto(Active) using cd.copy(server = Some(serverRef))
  }

  when(Active) {
    case Event(Login(username, password), cd: ClientData) =>
      cd.server match {
        case Some(server) => server ! Login(username, password)
        case None => log.warning("We are not registered on any server")
      }
      stay 
    case Event(JoinChannel(channel), _) =>
      stay
  }

}
