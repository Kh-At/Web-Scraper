package de.htwg.se.util.controllerUtils.commands



import de.htwg.se.util.FileIO
import de.htwg.se.controller.Controller
import de.htwg.se.config.Implicits.given
import de.htwg.se.util.viewUtils.Messages
import de.htwg.se.util.modelUtils.contentTyp._
import de.htwg.se.util.controllerUtils.memento._

trait Command {
  def execute(): Boolean
}

class ExitCommand(webController: Controller, messages: Messages) extends Command {
  def execute(): Boolean = {
    webController.passContent(new MessageTyp(messages.getByby()))
    System.exit(1)
    true
  }
}

class HelpCommand(webController: Controller, messages: Messages) extends Command {
  def execute(): Boolean = {
    webController.passContent(new MessageTyp(messages.getHelpmessage()))
    true
  }
}

class LoadCommand(webController: Controller, filename: String) extends Command {
  def execute(): Boolean = {
    webController.passContent(new FileContentTyp(filename)(using summon[FileIO]))
    true
  }
}

class ScrapeCommand(webController: Controller, url: String) extends Command {
  def execute(): Boolean = {
    webController.passContent(new WebsiteContentTyp(url))
    true
  }
}

class ShowInputCommand(webController: Controller, text: List[String]) extends Command {
  def execute(): Boolean = {
    webController.passContent(new UserInputTyp(text.mkString(" ")))
    true
  }
}

class SaveCommand(webController: Controller, filename: String , sitename: String) extends Command {
  def execute(): Boolean = {
    webController.saveCurrentContent(filename, sitename)
    true
  }
}

class ClearCommand(webController: Controller) extends Command {
  def execute(): Boolean = {
    webController.passContent(new UserInputTyp(""))
    true
  }
}

class UndoCommand(webController: Controller, messages: Messages, contentHistory: MementoHistory) extends Command {
  def execute(): Boolean = {
    contentHistory.undoHistory() match {
      case Some(memento) =>
        webController.passContent(new UserInputTyp(memento.content.mkString(" ")))
        true
      case None => 
        webController.passContent(new MessageTyp(messages.getNothingToUndo()))
        false
    }
  }
}
class RedoCommand(webController: Controller, messages: Messages, contentHistory: MementoHistory) extends Command {
  def execute(): Boolean = {
    contentHistory.redoHistory() match {
      case Some(memento) =>
        if (memento.command != null) memento.command.execute()
        else true
      case None => 
        webController.passContent(new MessageTyp(messages.getNothingToRedo()))
        false
    }
  }
}