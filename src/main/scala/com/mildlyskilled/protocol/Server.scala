package com.mildlyskilled.protocol

import akka.actor.ActorRef

object Server {

  sealed trait State

  case object Idle extends State

  case object Active extends State

  sealed trait Data

  case class Channels(channels: List[ActorRef]) extends Data


  sealed trait Message

  case object Start extends Message

  final case class Register(room: ActorRef) extends Message

}
