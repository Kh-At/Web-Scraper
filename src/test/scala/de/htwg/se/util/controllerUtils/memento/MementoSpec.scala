package de.htwg.se.util.controllerUtils.memento

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.util.controllerUtils.commands._

class MementoSpec extends AnyWordSpec with Matchers {
  
  class MockCommand extends Command {
    def execute(): Boolean = true
  }
  
  "Memento" should {
    "store content and command" in {
      val command = new MockCommand
      val memento = Memento(List("test", "content"), command)
      
      memento.content shouldBe List("test", "content")
      memento.command shouldBe command
      memento.getState shouldBe memento
    }
  }
  
  "MementoHistory" should {
    "save state" in {
      val history = new MementoHistory
      val memento = Memento(List("test"), new MockCommand)
      
      history.saveState(memento)
      history.canUndo shouldBe true
      history.canRedo shouldBe false
    }
    
    "undo history" in {
      val history = new MementoHistory
      val memento = Memento(List("test"), new MockCommand)
      
      history.saveState(memento)
      val undone = history.undoHistory()
      
      undone shouldBe Some(memento)
      history.canUndo shouldBe false
      history.canRedo shouldBe true
    }
    
    "redo history" in {
      val history = new MementoHistory
      val memento = Memento(List("test"), new MockCommand)
      
      history.saveState(memento)
      history.undoHistory()
      val redone = history.redoHistory()
      
      redone shouldBe Some(memento)
      history.canUndo shouldBe true
      history.canRedo shouldBe false
    }
    
    "handle empty undo" in {
      val history = new MementoHistory
      
      history.undoHistory() shouldBe None
      history.canUndo shouldBe false
    }
    
    "handle empty redo" in {
      val history = new MementoHistory
      
      history.redoHistory() shouldBe None
      history.canRedo shouldBe false
    }
  }
}