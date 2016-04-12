package com.mildlyskilled.protocol

import akka.actor.ActorRef

object Channel {

  sealed trait State

  case object Idle extends State

  case object Active extends State

  sealed trait Data

  final case class ChannelState(participants: List[ActorRef]) extends Data

  final case class RoomData(participants: List[ActorRef], channelName: String) extends Data

}
