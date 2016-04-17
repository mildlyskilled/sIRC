package com.mildlyskilled

import com.mildlyskilled.core.{ConsoleAction, Network}
import java.util.logging.Logger

import akka.actor.{ActorSystem, Props}
import com.mildlyskilled.actors.Server
import com.mildlyskilled.protocol.Message._
import com.typesafe.config.ConfigFactory

import scala.tools.jline.console.ConsoleReader

object ServerApplication extends App {
  implicit val logger = Logger.getLogger("Server Logger")
  logger.info(Console.GREEN_B + "Starting server" + Console.RESET)

  val ipSelection = ConsoleAction.promptSelection(Network.addressMap, "Select an IP address", Some("127.0.0.1"))
  val serverName = ConsoleAction.clean(ConsoleAction.promptInput("Name this server"))

  val serverConfiguration = ConfigFactory.parseString(s"""akka.remote.netty.tcp.hostname="$ipSelection" """)
  val defaultConfig = ConfigFactory.load.getConfig("server")
  val completeConfig = serverConfiguration.withFallback(defaultConfig)

  val system = ActorSystem("sIRC", completeConfig)
  val server = system.actorOf(Props[Server], serverName)
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
