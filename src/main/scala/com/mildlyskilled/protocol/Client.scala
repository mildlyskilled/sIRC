package com.mildlyskilled.protocol

import akka.actor.ActorRef

object Client extends Common {

  sealed trait State

  case object Idle extends State

  case object Active extends State

  sealed trait Data

  final case class ClientData(name: String, server: Option[ActorRef], channels: List[ActorRef]) extends Data

}
