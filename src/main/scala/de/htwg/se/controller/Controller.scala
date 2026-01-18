package de.htwg.se.controller

import scala.xml._
import de.htwg.se.model._
import de.htwg.se.util.viewUtils.Messages
import de.htwg.se.util.modelUtils.contentTyp._
import de.htwg.se.util.controllerUtils.memento._
import de.htwg.se.util.controllerUtils.commands._

import scala.util.{Using, Try}
import java.nio.file.{Files, Paths, StandardOpenOption}

class Controller(using model0: ScraperModelInterface, messages: Messages, contentHistory: MementoHistory)
  extends ControllerInterface {
  
  var commando: Command = _
  var model: ScraperModelInterface = model0
  
  def saveStateBeforeCommand(): Unit = contentHistory.saveState(Memento(model.currentContent, commando))

  def passContent(contentToPass: ContentTyp): ScraperModelInterface = {
      val newModel = model.processContent(contentToPass)
      this.model = newModel
      newModel
  }

  def Inputhandler(input: String): Option[String] = {
    val parts: List[String] = input.split("\\s+").toList
    parts match {
      case "save" :: Nil | "undo" :: Nil | "redo" :: Nil | "help" :: Nil | "exit" :: Nil =>
      case _ => saveStateBeforeCommand()
    }

    parts match {
      case "help" :: Nil => commando = new HelpCommand(this, messages)
      case "input" :: text => commando = new ShowInputCommand(this, text)
      case "save" :: filename :: sitename :: Nil => commando = new SaveCommand(this, filename, sitename)
      case "clear" :: Nil => commando = new ClearCommand(this)
      case "undo" :: Nil => commando = new UndoCommand(this, messages, contentHistory)
      case "redo" :: Nil => commando = new RedoCommand(this, messages, contentHistory)
      case "load" :: filename :: Nil => commando = new LoadCommand(this, filename)
      case "scrape" :: url :: Nil => commando = new ScrapeCommand(this, url)
      case "exit" :: Nil => commando = new ExitCommand(this, messages)
      case _ => println(s"Unbekannter Befehl: '$input'")
    }
    commando.execute()
    Some(input)
  }

  def saveCurrentContent(filename: String, sitename: String): Try[Unit] = {
    val content = model.currentContent.mkString("\n")

    val xml =
    <Website> 
      <Name> {sitename} </Name>
      <Content> {content} </Content>
    </Website>
    
    Try {
      if (filename.endsWith(".xml")) XML.save(filename, xml, "UTF-8", xmlDecl = true)
      else if (filename.endsWith(".json")) { }
      else {
        Using(Files.newBufferedWriter(Paths.get(filename), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
          writer => writer.write(content + "\n")}
        println(s"Content gespeichert in: $filename")
      }
    }.recover {
      case e: Exception =>
        println(s"Fehler beim Speichern: ${e.getMessage}")
    }
  }
}