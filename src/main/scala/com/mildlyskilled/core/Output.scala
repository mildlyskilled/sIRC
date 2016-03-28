package com.mildlyskilled.core

import scala.tools.jline.console.ConsoleReader

object Output {

  def displayOptions(optionsMap: Map[String, Any], message: String = "Select an option"): Any = {


    println(Console.GREEN)
    println(message)
    println("-" * message.length)
    println(Console.RESET)
    optionsMap.foreach {
      case (selector, option) => println(s"[${selector.toString}] $option")
    }

    val consoleReader = new ConsoleReader()

    val selection = for (
      ln <- Iterator.continually {
        consoleReader.readLine("> ")
      } if optionsMap.contains(ln)
    ) yield optionsMap(ln)

    selection.next()
  }
}
