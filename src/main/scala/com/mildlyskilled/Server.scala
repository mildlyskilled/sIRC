package com.mildlyskilled

import com.mildlyskilled.core.{Network, Output}
import java.util.logging.Logger

object Server extends App {
  implicit val logger = Logger.getLogger("Server Logger")
  logger.info(Console.GREEN_B + "Starting server" + Console.RESET)
  val selection = Output.displayOptions(Network.addressMap, "Select an IP address")

  println(selection)
}
