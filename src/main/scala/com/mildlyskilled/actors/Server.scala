package com.mildlyskilled.actors

import akka.actor.{ActorRef, FSM, Props}
import com.mildlyskilled.core.Auth
import com.mildlyskilled.protocol.Server._

class Server extends FSM[State, Data] {

  val globalChannel = context.actorOf(Props[Channel], "global")
  startWith(Idle, Channels(globalChannel::Nil))

  /**
    * @todo there should be an auth actor?
    */
  val auth = new Auth

  when(Idle) {
    case Event(Start, _) =>
      goto(Active)

    case Event(Info(msg), _) =>
      log.info(Console.GREEN + msg + Console.RESET)
      stay
  }

  when(Active) {
    case Event(Start, _) =>
      stay

    case Event(Info(msg), _) =>
      log.info(Console.GREEN + msg + Console.RESET)
      stay

    case Event(RegisterChannel(r), serverChannels@Channels(c)) =>
      stay using serverChannels.copy(channels = createChannel(r) :: c)

    case Event(Login(username, password), _) =>
      if (auth.login(username, password)) {
        globalChannel ! RegisterUser(sender)
        sender ! AuthenticationStatus("successful")
      }
      stay

    case Event(JoinChannel(c), s@Channels(ch)) =>
      s.channels.find(ch => ch.path.name == c) match {
        case Some(channel) =>
          channel.forward(RegisterUser(sender))
        case None => sender ! Warn(s"$c is not a valid channel")
      }

      stay

    case Event(RegisteredUsers, s@Channels(ch)) =>
      ch.find(c => c == globalChannel) match {
        case Some(c) => c.tell(RegisteredUsers, sender)
        case None => sender ! Warn("We do not have a global channel")
      }
      stay

  }


  onTransition {
    case _ -> Active =>
      log.info(Console.BLUE + "Server going ACTIVE" + Console.RESET)
    case _ -> Idle =>
      log.info(Console.BLUE + "Server going IDLE" + Console.RESET)
  }

  whenUnhandled {
    case Event(Start, _) =>
      goto(Active)
  }

  initialize()


  def createChannel(name: String): ActorRef =  context.actorOf(Props[Channel], name)

}
