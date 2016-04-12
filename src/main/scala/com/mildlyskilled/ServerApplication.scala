package com.mildlyskilled

import com.mildlyskilled.core.{ConsoleAction, Network}
import java.util.logging.Logger

import akka.actor.{ActorSystem, Props}
import com.mildlyskilled.actors.Server
import com.mildlyskilled.protocol.Message._

import scala.tools.jline.console.ConsoleReader

object ServerApplication extends App {
  implicit val logger = Logger.getLogger("Server Logger")
  logger.info(Console.GREEN_B + "Starting server" + Console.RESET)

  val ipSelection = ConsoleAction.promptSelection(Network.addressMap, "Select an IP address")
  val systemName = ConsoleAction.clean(ConsoleAction.promptInput("Name this server"))
  val system = ActorSystem("sIRC")
  val server = system.actorOf(Props[Server],systemName)
  val cReader = new ConsoleReader()

  Iterator.continually(cReader.readLine("> ")).takeWhile(_ != "/exit").foreach {
    case "/start" =>
      server ! Start
    case "/users" =>
      server.tell(RegisteredUsers, server)
    case "/channels" =>
      server.tell(ListChannels, server)
    case "/stop" =>
      server ! Stop
    case "/shutdown" =>
      system.terminate()
    case userCommandMessageRegex("add", c) =>
      server.tell(RegisterChannel(ConsoleAction.clean(c)), server)
    case msg =>
      server ! Info(msg)
  }

  system.terminate()
}
