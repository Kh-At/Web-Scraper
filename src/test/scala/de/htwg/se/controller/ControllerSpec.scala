package de.htwg.se.controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ControllerSpec extends AnyWordSpec with Matchers {
  
  "Controller" should {
    "be instantiable" in {
      val model = de.htwg.se.model.WebScraperModel(Nil)
      val messages = new de.htwg.se.util.viewUtils.Messages()
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      val fileIO = new de.htwg.se.util.JsonFormat()
      
      val controller = new Controller(using model, messages, history, fileIO)
      controller should not be null
    }
    
    "handle at least one command" in {
      val model = de.htwg.se.model.WebScraperModel(Nil)
      val messages = new de.htwg.se.util.viewUtils.Messages()
      val history = new de.htwg.se.util.controllerUtils.memento.MementoHistory()
      val fileIO = new de.htwg.se.util.JsonFormat()
      
      val controller = new Controller(using model, messages, history, fileIO)
      val result = controller.Inputhandler("help")
      result shouldBe Some("help")
    }
  }
}