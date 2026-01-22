package de.htwg.se.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Success

class ControllerInterfaceSpec extends AnyWordSpec with Matchers {
  
  class MockController extends ControllerInterface {
    var lastInput: String = ""
    var savedContent: (String, String) = ("", "")
    
    def Inputhandler(input: String): Option[String] = {
      lastInput = input
      Some(input)
    }
    
    def saveCurrentContent(filename: String, sitename: String): scala.util.Try[Unit] = {
      savedContent = (filename, sitename)
      Success(())
    }
    
    def passContent(contentToPass: de.htwg.se.util.modelUtils.contentTyp.ContentTyp): 
      de.htwg.se.model.ScraperModelInterface = {
      de.htwg.se.model.WebScraperModel(Nil)
    }
  }
  
  "ControllerInterface" should {
    "define required methods" in {
      val mock = new MockController
      
      mock.Inputhandler("test") shouldBe Some("test")
      mock.saveCurrentContent("test.txt", "site").isSuccess shouldBe true
      mock.passContent(new de.htwg.se.util.modelUtils.contentTyp.UserInputTyp("test")) should not be null
    }
  }
}