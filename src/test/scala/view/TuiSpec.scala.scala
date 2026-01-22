package de.htwg.se.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.util.modelUtils.ContentStatus

class TuiSpec extends AnyWordSpec with Matchers {
  
  class MockController extends de.htwg.se.controller.ControllerInterface {
    var lastCommand: String = ""
    
    def Inputhandler(input: String): Option[String] = {
      lastCommand = input
      Some(input)
    }
    
    def saveCurrentContent(filename: String, sitename: String): scala.util.Try[Unit] = 
      scala.util.Success(())
    
    def passContent(contentToPass: de.htwg.se.util.modelUtils.contentTyp.ContentTyp): 
      de.htwg.se.model.ScraperModelInterface = {
      de.htwg.se.model.WebScraperModel(Nil)
    }
  }
  
  "Tui" should {
    "format lines correctly" in {
      given controller: MockController = new MockController
      val tui = new Tui(20, 10)
      
      val formatted = tui.formatLine("test", 10)
      formatted should have length 10
      formatted should startWith("test")
    }
    
    "split long words" in {
      given controller: MockController = new MockController
      val tui = new Tui(20, 10)
      
      val split = tui.splitLongWord("verylongword", 5)
      split should contain allOf("veryl", "ongwo", "rd")
    }
    
    "wrap text" in {
      given controller: MockController = new MockController
      val tui = new Tui(20, 10)
      
      val wrapped = tui.wrapText("This is a test sentence", 10)
      wrapped should have size 3
    }
    
    "build tower display" in {
      given controller: MockController = new MockController
      val tui = new Tui(10, 5)
      
      val tower = tui.build_tower(10, 5, List("line1", "line2"))
      tower should include("line1")
      tower should include("line2")
    }
    
    "update content" in {
      given controller: MockController = new MockController
      val tui = new Tui(20, 10)
      
      tui.update(List("new content"), ContentStatus.success, "test")
      tui.currentContent shouldBe List("new content")
      tui.currentStatus shouldBe ContentStatus.success
      tui.currentSourceType shouldBe "test"
    }
    
    "implement Observer trait" in {
      given controller: MockController = new MockController
      val tui = new Tui(20, 10)
      
      tui.isInstanceOf[de.htwg.se.util.viewUtils.Observer] shouldBe true
    }
  }
}