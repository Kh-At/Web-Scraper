package de.htwg.se.util.controllerUtils.commands

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.controller.Controller
import de.htwg.se.util.viewUtils.Messages

class CommandSpec extends AnyWordSpec with Matchers {
  
  class MockController extends Controller(using 
    de.htwg.se.model.WebScraperModel(Nil),
    new Messages,
    new de.htwg.se.util.controllerUtils.memento.MementoHistory,
    de.htwg.se.util.JsonFormat()) {
    
    var lastExecuted: String = ""
    
    override def passContent(content: de.htwg.se.util.modelUtils.contentTyp.ContentTyp): 
      de.htwg.se.model.ScraperModelInterface = {
      lastExecuted = "passContent"
      super.passContent(content)
    }
  }
  
  "ExitCommand" should {
    "execute successfully" in {
      val controller = new MockController
      val command = new ExitCommand(controller, new Messages)
      
      command.execute() shouldBe true
    }
  }
  
  "HelpCommand" should {
    "execute successfully" in {
      val controller = new MockController
      val command = new HelpCommand(controller, new Messages)
      
      command.execute() shouldBe true
    }
  }
  
  "LoadCommand" should {
    "execute successfully" in {
      val controller = new MockController
      val command = new LoadCommand(controller, "test.txt")
      
      command.execute() shouldBe true
    }
  }
  
  "ScrapeCommand" should {
    "execute successfully" in {
      val controller = new MockController
      val command = new ScrapeCommand(controller, "http://test.com")
      
      command.execute() shouldBe true
    }
  }
  
  "ShowInputCommand" should {
    "execute successfully" in {
      val controller = new MockController
      val command = new ShowInputCommand(controller, List("test", "input"))
      
      command.execute() shouldBe true
    }
  }
  
  "SaveCommand" should {
    "execute successfully" in {
      val controller = new MockController
      val command = new SaveCommand(controller, "test.txt", "TestSite")
      
      command.execute() shouldBe true
    }
  }
  
  "ClearCommand" should {
    "execute successfully" in {
      val controller = new MockController
      val command = new ClearCommand(controller)
      
      command.execute() shouldBe true
    }
  }
  
  "Command trait" should {
    "be implemented by all command classes" in {
      val controller = new MockController
      val messages = new Messages
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory
      
      val exitCmd = new ExitCommand(controller, messages)
      val helpCmd = new HelpCommand(controller, messages)
      val clearCmd = new ClearCommand(controller)
      
      exitCmd.isInstanceOf[Command] shouldBe true
      helpCmd.isInstanceOf[Command] shouldBe true
      clearCmd.isInstanceOf[Command] shouldBe true
    }
  }
}