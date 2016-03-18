package com.mildlyskilled.protocol

import akka.actor.ActorRef

object Room {

  sealed trait Data

  final case class RoomData(participants: List[ActorRef], roomName: String) extends Data

}
