package com.mildlyskilled


import akka.actor.{ActorSystem, Props}
import com.mildlyskilled.actors.Client
import com.mildlyskilled.core.{ConsoleAction, Network}
import com.mildlyskilled.protocol.Message._
import com.typesafe.config.ConfigFactory

import java.util.logging.Logger
import scala.tools.jline.console.ConsoleReader

object ClientApplication extends App {

  implicit val logger = Logger.getLogger("Client Logger")
  logger.info(Console.GREEN_B + "Starting client" + Console.RESET)
  val username = ConsoleAction.promptInput("Username")
  val serverName = ConsoleAction.promptInput("Server Name")
  val ipSelection = ConsoleAction.promptSelection(Network.addressMap, "Select an IP address", Some("127.0.0.1"))

  val clientConfig = ConfigFactory.parseString(s"""akka.remote.netty.tcp.hostname="$ipSelection" """)
  val defaultConfig = ConfigFactory.load.getConfig("client")
  val completeConfig = clientConfig.withFallback(defaultConfig)

  val system = ActorSystem("sIRC", completeConfig)
  val client = system.actorOf(Props[Client], username)

  val serverconfig = ConfigFactory.load.getConfig("server")
  val serverAddress = ConsoleAction.promptInput(s"Server IP Address or hostname [$ipSelection]", s"$ipSelection")
  val serverPort = serverconfig.getString("akka.remote.netty.tcp.port")
  val serverPath = s"akka.tcp://sIRC@$serverAddress:$serverPort/user/$serverName"
  val server = system.actorSelection(serverPath) // <-- this is where we get the server reference

  server.tell(Login("username", "password"), client)

  val cReader = new ConsoleReader

  Iterator.continually(cReader.readLine("> ")).takeWhile(_ != "/exit").foreach {
    case "/start" =>
      server ! Start
    case "/users" =>
      server.tell(RegisteredUsers, client)
    case "/channels" =>
      server.tell(ListChannels, client)
    case "/leave" =>
      server.tell(Leave, client)
    case msg =>
      server ! Info(msg)
    case userCommandMessageRegex("join", c) =>
      server.tell(JoinChannel(ConsoleAction.clean(c)), client)
  }

  system.terminate()
}
