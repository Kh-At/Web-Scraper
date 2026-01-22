package de.htwg.se.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import scala.util.Success

class ControllerSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach {
  
  private var controller: Controller = _
  private val testMessages = new de.htwg.se.util.viewUtils.Messages
  
  override def beforeEach(): Unit = {
    val testModel = de.htwg.se.model.WebScraperModel(Nil)
    val testHistory = new de.htwg.se.util.controllerUtils.memento.MementoHistory
    controller = new Controller(using testModel, testMessages, testHistory, 
      de.htwg.se.util.JsonFormat())
  }
  
  "Controller" should {
    "handle help command" in {
      val result = controller.Inputhandler("help")
      result shouldBe Some("help")
    }
    
    "handle input command" in {
      val result = controller.Inputhandler("input test message")
      result shouldBe Some("input test message")
    }
    
    "handle clear command" in {
      val result = controller.Inputhandler("clear")
      result shouldBe Some("clear")
    }
    
    "handle save command" in {
      val result = controller.Inputhandler("save test.txt Website")
      result shouldBe Some("save test.txt Website")
    }
    
    "pass content to model" in {
      val content = new de.htwg.se.util.modelUtils.contentTyp.UserInputTyp("test")
      val newModel = controller.passContent(content)
      newModel should not be null
    }
    
    "save current content" in {
      val result = controller.saveCurrentContent("test.txt", "TestSite")
      result.isSuccess shouldBe true
    }
  }
}