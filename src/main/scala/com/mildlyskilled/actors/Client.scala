package com.mildlyskilled.actors

import akka.actor.FSM
import com.mildlyskilled.core.ConsoleAction
import com.mildlyskilled.protocol.Client._
import com.mildlyskilled.protocol.Message._


class Client extends FSM[State, Data] {

  startWith(Idle, ClientData(self.path.name, None, Nil))

  when(Idle) {
    case Event(Connect(serverRef), cd: ClientData) =>
      log.info(Console.GREEN + s"Sent register message to server at ${serverRef.path.name}" + Console.RESET)
      goto(Active) using cd.copy(server = Some(serverRef))
    case Event(AuthenticationStatus(x), cd: ClientData) =>
      goto(Active)
    case Event(Info(msg), _) =>
      log.info(Console.YELLOW + msg + Console.RESET)
      stay()
  }

  when(Active) {
    case Event(Login(username, password), cd: ClientData) =>
      cd.server match {
        case Some(server) => server ! Login(username, password)
        case None => log.warning("We are not registered on any server")
      }
      stay()
    case Event(JoinChannel(channel), _) =>
      stay()
    case Event(AuthenticationStatus(x), cd: ClientData) =>
      stay()
    case Event(Connect(serverRef), cd: ClientData) =>
      log.info(Console.GREEN + s"Sent register message to server at ${serverRef.path.name}" + Console.RESET)
      goto(Active) using cd.copy(server = Some(serverRef))
    case Event(Stop, _) =>
      context.stop(self)
      stay() // eh?
    case Event(Info(msg), _) =>
      log.info(Console.YELLOW + msg + Console.RESET)
      stay()
    case Event(RegisteredUsers(listOfUsers), _) =>
      ConsoleAction.outputList(listOfUsers, "Users")
      stay()
    case Event(ChannelList(listOfChannels), _) =>
      ConsoleAction.outputList(listOfChannels, "Channels")
      stay()
  }

  onTransition {
    case _ -> Active =>
      log.info(Console.BLUE + "Client going ACTIVE" + Console.RESET)
    case _ -> Idle =>
      log.info(Console.BLUE + "Client going IDLE" + Console.RESET)
  }

}
