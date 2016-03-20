package com.mildlyskilled.protocol

import akka.actor.ActorRef

object Server extends Global {

  sealed trait State

  case object Idle extends State

  case object Active extends State

  sealed trait Data

  case class Channels(channels: List[ActorRef]) extends Data


}
