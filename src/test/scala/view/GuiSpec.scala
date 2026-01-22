package de.htwg.se.view

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.util.modelUtils.ContentStatus

class GuiSpec extends AnyWordSpec with Matchers {
  
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
  
  "Gui" should {
    "implement Observer trait" in {
      given controller: MockController = new MockController
      val gui = new Gui
      
      gui.isInstanceOf[de.htwg.se.util.viewUtils.Observer] shouldBe true
    }
    
    "have update method" in {
      given controller: MockController = new MockController
      val gui = new Gui
      
      // Test that update method exists and can be called
      gui.update(List("test content"), ContentStatus.success, "test")
      succeed
    }
  }
}