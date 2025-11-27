package de.htwg.se

import scala.io.StdIn.readLine
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.util.{Using, Try}

class Controller(model: WebScraperModel, view: Tui) {
  
  val prompt = ">"
  
  def start(): Unit = {
    mainLoop()
  }
  
  def mainLoop(): Unit = {
    var running = true
    model.processContent(MessageTyp(model.getWelcomeMessage))
    while (running) {
      
      print(prompt)
      val input = readLine().trim
      handleUserInput(input) match {
        case false => running = false
        case true =>
      }
    }
  }

  def handleUserInput(input: String): Boolean = {
    val parts = input.split("\\s+").toList

    parts match {
      case "help" :: Nil => model.processContent(new MessageTyp(model.getHelpMessage))
        true
      
      case "load" :: filename :: Nil => val source = new FileContentTyp(filename)
        model.processContent(source)
        true
      
      case "scrape" :: url :: Nil => model.processContent(new WebsiteContentTyp(url))
        true
      
      case "input" :: text => model.processContent(new UserInputTyp(text.mkString(" ")))
        true

      case "save" :: filename :: Nil => saveCurrentContent(filename)
        true
        
      case "clear" :: Nil => model.processContent(new UserInputTyp(""))
        true

      case "exit" :: Nil => model.processContent(new MessageTyp("auf wiedersehen!"))
        false

      case _ => model.processContent(new MessageTyp(model.getHelpMessage))
        println(s"Unbekannter Befehl: '$input'")
        true
    }
  }

  private def saveCurrentContent(filename: String): Try[Unit] = {
    val content = model.getContent.mkString("\n")
    Try {
      Using(Files.newBufferedWriter(Paths.get(filename), StandardOpenOption.CREATE, 
        StandardOpenOption.TRUNCATE_EXISTING)) { writer => writer.write(content + "\n")
      }
      println(s"Content gespeichert in: $filename")
    }.recover {
      case e: Exception =>
        println(s"Fehler beim Speichern: ${e.getMessage}")
    }
  }
}