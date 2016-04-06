package com.mildlyskilled.core

import scala.tools.jline.console.ConsoleReader

object ConsoleAction {

  private val consoleReader = new ConsoleReader()

  def getConsoleReader: ConsoleReader = consoleReader

  def promptSelection(optionsMap: Map[String, Any], message: String = "Select an option"): Any = {

    println(Console.GREEN)
    println(message)
    println("-" * message.length)

    if (optionsMap.isEmpty) {
      println("No options available at the moment")
    } else{
      optionsMap.foreach {
        case (selector, option) => println(s"[${selector.toString}] $option")
      }

      println(Console.RESET)

      val selection = for (
        ln <- Iterator.continually {
          consoleReader.readLine("> ")
        } if optionsMap.contains(ln)
      ) yield optionsMap(ln)

      selection.next()
    }


  }

  def promptInput(msg:String = "Type in some input"): String = {
    val input = for(in <- Iterator.continually(getConsoleReader.readLine(s"$msg > ")).takeWhile(_ != "quit")) yield in
    input.next()
  }

}
