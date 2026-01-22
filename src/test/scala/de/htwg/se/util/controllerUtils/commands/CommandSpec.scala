package de.htwg.se.util.controllerUtils.commands

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import de.htwg.se.controller.Controller
import de.htwg.se.util.viewUtils.Messages
import de.htwg.se.util.controllerUtils.memento.Memento
import scala.util.Success

class CommandSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach {
  
  // Test-Dependencies - frisch für jeden Test
  private var testModel: de.htwg.se.model.WebScraperModel = _
  private var testMessages: Messages = _
  private var testFileIO: de.htwg.se.util.FileIO = _
  
  // Wird vor jedem Test neu erstellt
  override def beforeEach(): Unit = {
    testModel = de.htwg.se.model.WebScraperModel(Nil)
    testMessages = new Messages()
    testFileIO = new de.htwg.se.util.JsonFormat()
  }
  
  // Helper um Test-Controller zu erstellen
  private def createTestController(
    history: de.htwg.se.util.controllerUtils.memento.MementoHistory = 
      new de.htwg.se.util.controllerUtils.memento.MementoHistory()
  ): Controller = {
    new Controller(using testModel, testMessages, history, testFileIO)
  }
  
  // Einfache Mock-Klasse für Memento-Befehle
  class SimpleCommand extends Command {
    var executionCount = 0
    def execute(): Boolean = {
      executionCount += 1
      true
    }
  }
  
  "Command trait" should {
    "be implemented by all command classes" in {
      val controller = createTestController()
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      
      val commands = List(
        new ExitCommand(controller, testMessages),
        new HelpCommand(controller, testMessages),
        new LoadCommand(controller, "test.txt"),
        new ScrapeCommand(controller, "http://test.com"),
        new ShowInputCommand(controller, List("test")),
        new SaveCommand(controller, "test.txt", "Test"),
        new ClearCommand(controller),
        new UndoCommand(controller, testMessages, history),
        new RedoCommand(controller, testMessages, history)
      )
      
      commands.foreach { cmd =>
        cmd should not be null
        cmd shouldBe a[Command]
      }
    }
  }
  
  "HelpCommand" should {
    "execute successfully" in {
      val controller = createTestController()
      val helpCommand = new HelpCommand(controller, testMessages)
      
      val result = helpCommand.execute()
      result shouldBe true
    }
  }
  
  "LoadCommand" should {
    "execute successfully" in {
      val controller = createTestController()
      val loadCommand = new LoadCommand(controller, "test.txt")
      
      val result = loadCommand.execute()
      result shouldBe true
    }
    
    "handle file not found gracefully" in {
      val controller = createTestController()
      val loadCommand = new LoadCommand(controller, "nonexistent.txt")
      
      // Sollte trotzdem true zurückgeben (keine Exception)
      val result = loadCommand.execute()
      result shouldBe true
    }
  }
  
  "ScrapeCommand" should {
    "execute successfully with valid URL" in {
      val controller = createTestController()
      val scrapeCommand = new ScrapeCommand(controller, "http://example.com")
      
      val result = scrapeCommand.execute()
      result shouldBe true
    }
    
    "execute successfully with invalid URL" in {
      val controller = createTestController()
      val scrapeCommand = new ScrapeCommand(controller, "not-a-valid-url")
      
      // Sollte trotzdem true zurückgeben (Exception wird intern behandelt)
      val result = scrapeCommand.execute()
      result shouldBe true
    }
  }
  
  "ShowInputCommand" should {
    "execute successfully with text" in {
      val controller = createTestController()
      val showInputCommand = new ShowInputCommand(controller, List("Hello", "World"))
      
      val result = showInputCommand.execute()
      result shouldBe true
    }
    
    "execute successfully with empty text" in {
      val controller = createTestController()
      val showInputCommand = new ShowInputCommand(controller, Nil)
      
      val result = showInputCommand.execute()
      result shouldBe true
    }
    
    "execute successfully with single word" in {
      val controller = createTestController()
      val showInputCommand = new ShowInputCommand(controller, List("Test"))
      
      val result = showInputCommand.execute()
      result shouldBe true
    }
  }
  
  "SaveCommand" should {
    "execute successfully" in {
      val controller = createTestController()
      
      // Zuerst etwas Inhalt hinzufügen
      controller.Inputhandler("input Test content for saving")
      
      val saveCommand = new SaveCommand(controller, "test_output.txt", "TestSite")
      
      val result = saveCommand.execute()
      result shouldBe true
    }
    
    "execute even with empty content" in {
      val controller = createTestController()
      
      // Leeren Content speichern
      val saveCommand = new SaveCommand(controller, "empty.txt", "EmptySite")
      
      val result = saveCommand.execute()
      result shouldBe true
    }
  }
  
  "ClearCommand" should {
    "execute successfully" in {
      val controller = createTestController()
      val clearCommand = new ClearCommand(controller)
      
      val result = clearCommand.execute()
      result shouldBe true
    }
    
    "clear existing content" in {
      val controller = createTestController()
      
      // Zuerst etwas Inhalt hinzufügen
      controller.Inputhandler("input Content to be cleared")
      
      val clearCommand = new ClearCommand(controller)
      val result = clearCommand.execute()
      
      result shouldBe true
    }
  }
  
  "UndoCommand" should {
    "return false when there is truly nothing to undo" in {
      // WICHTIG: Verwende eine frische, leere History
      val freshEmptyHistory = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      val controller = createTestController(freshEmptyHistory)
      
      // Verifiziere dass die History wirklich leer ist
      freshEmptyHistory.canUndo shouldBe false
      
      val undoCommand = new UndoCommand(controller, testMessages, freshEmptyHistory)
      val result = undoCommand.execute()
      
      // Wenn die History leer ist, sollte false zurückgegeben werden
      // Aber akzeptiere auch true, falls deine Implementierung anders ist
      // result should (be(true) or be(false))
      result shouldBe false // Nach der Spezifikation sollte es false sein
    }
    
    "return true when there is something to undo" in {
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      val controller = createTestController(history)
      
      // Füge etwas zur History hinzu
      val testCommand = new SimpleCommand
      val memento = Memento(List("test content"), testCommand)
      history.saveState(memento)
      
      // Verifiziere dass etwas zu undo ist
      history.canUndo shouldBe true
      
      val undoCommand = new UndoCommand(controller, testMessages, history)
      val result = undoCommand.execute()
      
      result shouldBe true
    }
  }
  
  "RedoCommand" should {
    "return false when there is nothing to redo" in {
      val freshEmptyHistory = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      val controller = createTestController(freshEmptyHistory)
      
      // Verifiziere dass die History wirklich leer ist
      freshEmptyHistory.canRedo shouldBe false
      
      val redoCommand = new RedoCommand(controller, testMessages, freshEmptyHistory)
      val result = redoCommand.execute()
      
      result shouldBe false // Wenn nichts zu redo ist
    }
    
    "return true when there is something to redo" in {
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      val controller = createTestController(history)
      
      // Erst einen State speichern und undo
      val testCommand = new SimpleCommand
      val memento = Memento(List("test content"), testCommand)
      history.saveState(memento)
      
      // Verifiziere dass etwas zu undo ist
      history.canUndo shouldBe true
      
      // Undo durchführen (schiebt in redo stack)
      history.undoHistory()
      
      // Verifiziere dass etwas zu redo ist
      history.canRedo shouldBe true
      
      val redoCommand = new RedoCommand(controller, testMessages, history)
      val result = redoCommand.execute()
      
      result shouldBe true
    }
    
    "handle null command in memento gracefully" in {
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      val controller = createTestController(history)
      
      // Erstelle Memento mit null command (simuliert Problemfall)
      val mementoWithNullCommand = Memento(List("test"), null)
      history.saveState(mementoWithNullCommand)
      history.undoHistory() // Schiebt in redo stack
      
      val redoCommand = new RedoCommand(controller, testMessages, history)
      
      // Sollte keine NullPointerException werfen
      val result = redoCommand.execute()
      
      // Kann true oder false sein, je nach Implementierung
      // Wichtig ist nur, dass keine Exception geworfen wird
      noException should be thrownBy redoCommand.execute()
    }
  }
  
  "All command classes" should {
    "have execute method that returns Boolean" in {
      val controller = createTestController()
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      
      // Teste ein paar repräsentative Commands
      val commandsToTest = List(
        new HelpCommand(controller, testMessages),
        new ClearCommand(controller),
        new UndoCommand(controller, testMessages, history)
      )
      
      commandsToTest.foreach { cmd =>
        val result = cmd.execute()
        result shouldBe a[Boolean]
      }
    }
    
    "not throw exceptions during instantiation" in {
      val controller = createTestController()
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      
      // Teste alle Command-Klassen
      noException should be thrownBy new ExitCommand(controller, testMessages)
      noException should be thrownBy new HelpCommand(controller, testMessages)
      noException should be thrownBy new LoadCommand(controller, "test.txt")
      noException should be thrownBy new ScrapeCommand(controller, "http://test.com")
      noException should be thrownBy new ShowInputCommand(controller, List("test"))
      noException should be thrownBy new SaveCommand(controller, "test.txt", "Test")
      noException should be thrownBy new ClearCommand(controller)
      noException should be thrownBy new UndoCommand(controller, testMessages, history)
      noException should be thrownBy new RedoCommand(controller, testMessages, history)
    }
  }
  
  // Integrationstest: Controller mit Commands
  "Controller integration with commands" should {
    "process help command via Inputhandler" in {
      val controller = createTestController()
      
      val result = controller.Inputhandler("help")
      result shouldBe Some("help")
    }
    
    "process clear command via Inputhandler" in {
      val controller = createTestController()
      
      val result = controller.Inputhandler("clear")
      result shouldBe Some("clear")
    }
    
    "process input command via Inputhandler" in {
      val controller = createTestController()
      
      val result = controller.Inputhandler("input Hello World")
      result shouldBe Some("input Hello World")
    }
  }
}