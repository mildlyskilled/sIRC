package com.mildlyskilled

import java.util.logging.Logger
import com.mildlyskilled.core.{ConsoleAction, Network}

object ClientApplication extends  App {

  implicit val logger = Logger.getLogger("Client Logger")
  logger.info(Console.GREEN_B + "Starting client" + Console.RESET)
  val username = ConsoleAction.promptInput("Username")

  println(username)
}
