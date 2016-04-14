package com.mildlyskilled.protocol

import akka.actor.ActorRef

/**
  * Created by kwabena on 19/03/2016.
  */
object Message {

  sealed trait Message

  case object Start extends Message

  case object RegisteredUsers extends Message

  case object ListChannels extends Message

  case object Leave extends Message

  case object Stop extends Message

  final case class Info(msg: String) extends Message

  final case class Warn(msg: String) extends Message

  final case class RegisterUser(user: ActorRef) extends Message

  final case class RemoveUser(user: ActorRef) extends Message

  final case class RegisteredUsers(users: List[String]) extends Message

  final case class RegisterChannel(channel: String) extends Message

  final case class Login(username: String, password: String) extends Message

  final case class AuthenticationStatus(status: String) extends Message

  final case class JoinChannel(channel: String) extends Message

  final case class Connect(server: ActorRef) extends Message

  final case class ChannelList(channels: List[ActorRef]) extends Message

  val privateMessageRegex = """^@([^\s]+) (.*)$""".r

  val userCommandMessageRegex = """^:([^\s]+) ([^\s]+)$""".r

}
