package de.htwg.se

import scala.io.StdIn.readLine
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.util.{Using, Try}

class Controller(model: WebScraperModel, view: Tui) {
  
  val prompt = ">"
  val helpMessage:String = 
      "|Verfügbare Befehle:" + "\n"
    + "|  load <dateiname>    - Lädt Inhalt aus Datei " + "\n"
    + "|  scrape <url>        - Scraped Inhalt von Website" + "\n"
    + "|  input oder i        - Startet Input-Modus für mehrzeiligen Text" + "\n"
    + "|  input <text>        - Verarbeitet direkte Texteingabe (einzeilig)" + "\n"
    + "|  save <dateiname>    - Speichert aktuellen Content" + "\n"
    + "|  clear               - Leert den Content" + "\n"
    + "|  help                - Zeigt diese Hilfe" + "\n"
    + "|  exit/quit           - Beendet das Programm" + "\n"
  
  def start(): Unit = {
    mainLoop()
  }
  
  private def mainLoop(): Unit = {
    var running = true
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
      case "help" :: Nil => model.processContent(new InternelMessageTyp(helpMessage))
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

      case "exit" :: Nil => println("byby boi!")
        false

      case _ => model.processContent(new InternelMessageTyp(helpMessage))
        println(s"Unbekannter Befehl: '$input'")
        true
    }
  }

  private def saveCurrentContent(filename: String): Unit = {
    val content = model.getContent.mkString("\n")
    Try {
      Using(Files.newBufferedWriter(Paths.get(filename), 
             StandardOpenOption.CREATE, 
             StandardOpenOption.TRUNCATE_EXISTING)) { writer =>
        writer.write(content + "\n")
      }
      println(s"Content gespeichert in: $filename")
    }.recover {
      case e: Exception =>
        println(s"Fehler beim Speichern: ${e.getMessage}")
    }
  }
}