package com.mildlyskilled.core

import scala.tools.jline.console.ConsoleReader

object ConsoleAction {

  private val consoleReader = new ConsoleReader()

  def getConsoleReader: ConsoleReader = consoleReader

  def promptSelection(optionsMap: Map[String, Any], message: String = "Select an option", default: Option[String] = None): Any = {

    println(Console.GREEN)
    println(message)
    println("-" * message.length)

    if (optionsMap.isEmpty) {
      default match {
        case Some(defaultValue) =>
          defaultValue
        case None =>
          println(Console.RED + "No options available at the moment" + Console.RESET)
      }

    } else {
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

  def promptInput(msg: String = "Type in some input", default: String = "input", exitString: String = "quit"): String = {
    val input = for (in <- Iterator.continually(getConsoleReader.readLine(s"$msg > "))
      .takeWhile(_ != exitString)) yield in
    input.next.trim match {
      case "" => default
      case x => x
    }
  }

  def outputList(list: List[AnyRef], name: String): Unit = {
    println(Console.YELLOW)
    println(s"list of $name")
    list foreach println
    println(Console.RESET)
  }

  def clean(s: String): String = s.replaceAll("""[\W_]+""", "")

}
