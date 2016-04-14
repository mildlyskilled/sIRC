package com.mildlyskilled

import java.util.logging.Logger

import akka.actor.{ActorSystem, Props}
import com.mildlyskilled.actors.Client
import com.mildlyskilled.core.{ConsoleAction, Network}
import com.mildlyskilled.protocol.Message.Login
import com.typesafe.config.ConfigFactory

object ClientApplication extends  App {

  implicit val logger = Logger.getLogger("Client Logger")
  logger.info(Console.GREEN_B + "Starting client" + Console.RESET)
  val username = ConsoleAction.promptInput("Username")
  val serverName= ConsoleAction.promptInput("Server Name")
  val ipSelection = ConsoleAction.promptSelection(Network.addressMap, "Select an IP address")

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

}
