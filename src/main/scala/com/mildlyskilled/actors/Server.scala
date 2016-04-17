package com.mildlyskilled.actors

import akka.actor.{ActorRef, FSM, InvalidActorNameException, Props, Terminated}
import com.mildlyskilled.core.{Auth, ConsoleAction}
import com.mildlyskilled.protocol.Server._
import com.mildlyskilled.protocol.Message._

import scala.util.{Failure, Success, Try}

class Server extends FSM[State, Data] {

  val globalChannel = context.actorOf(Props[Channel], "global")
  startWith(Idle, Channels(globalChannel :: Nil))

  /**
    * @todo there should be an auth actor/service?
    */
  val auth = new Auth

  when(Idle) {
    case Event(Start, s@Channels(channelList)) =>
      channelList.foreach(c => c ! Start)
      goto(Active) using s

    case Event(Info(msg), _) =>
      log.info(Console.YELLOW_B + msg + Console.RESET)
      stay()
  }

  when(Active) {
    case Event(Start, s@Channels(channelList)) =>
      globalChannel ! Start
      stay using s

    case Event(Info(msg), _) =>
      log.info(Console.YELLOW + msg + Console.RESET)
      stay()

    case Event(RegisterChannel(r), serverChannels@Channels(c)) =>
      log.info(s"Adding new channel $r")
      if (c.map(c => c.path.name).contains(r)) {
        sender ! Info("This channel already exists")
        stay using serverChannels.copy(channels = c)
      } else {
        stay using serverChannels.copy(channels = createChannel(r) :: c)
      }

    case Event(Login(username, password), _) =>
      if (auth.login(username, password)) {
        globalChannel ! RegisterUser(sender)
        context.watch(sender())
        sender ! AuthenticationStatus("successful")
      }
      stay()

    case Event(JoinChannel(c), s@Channels(ch)) =>
      s.channels.find(ch => ch.path.name == c) match {
        case Some(channel) =>
          channel.forward(RegisterUser(sender))
        case None => sender ! Warn(s"$c is not a valid channel")
      }
      stay()

    case Event(RegisteredUsers, s@Channels(ch)) =>
      ch.find(_ == globalChannel) match {
        case Some(c) => c.tell(RegisteredUsers, sender)
        case None => sender ! Warn("We do not have a global channel")
      }
      stay()

    case Event(RegisteredUsers(users), _) =>
      ConsoleAction.outputList(users, "Users")
      stay()

    case Event(ListChannels, Channels(ch)) =>
      sender ! ChannelList(ch.map(c => c.path.name))
      stay()

    case Event(ChannelList(c), _) =>
      ConsoleAction.outputList(c, "Channels")
      stay()
    case Event(Leave, s@Channels(ch)) =>
      ch.foreach(_ ! RemoveUser(sender))
      stay()

    case Event(Stop, _) =>
      context.stop(self)
      stay // um OK

    case Event(Terminated(client), s@Channels(ch)) =>
      ch.foreach(_ ! RemoveUser(client))
      stay()
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


  def createChannel(name: String): ActorRef = context.actorOf(Props[Channel], name)

}
