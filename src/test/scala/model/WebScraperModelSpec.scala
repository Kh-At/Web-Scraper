package de.htwg.se.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.util.modelUtils.contentTyp._
import de.htwg.se.util.modelUtils.ContentStatus

class WebScraperModelSpec extends AnyWordSpec with Matchers {
  
  class TestObserver extends de.htwg.se.util.viewUtils.Observer {
    var updates: List[(List[String], ContentStatus, String)] = Nil
    
    def update(content: List[String], status: ContentStatus, sourceType: String): Unit = {
      updates = (content, status, sourceType) :: updates
    }
  }
  
  "WebScraperModel" should {
    "create with initial content" in {
      val content = List("line1", "line2")
      val model = WebScraperModel(content)
      
      model.currentContent shouldBe content
      model.getCurrentContent() shouldBe content
    }
    
    "process user input content" in {
      val model = WebScraperModel(Nil)
      val content = new UserInputTyp("test input")
      
      val result = model.processContent(content)
      result should not be null
      result.getCurrentContent() should contain("test input")
    }
    
    "process message content" in {
      val model = WebScraperModel(Nil)
      val content = new MessageTyp("test message")
      
      val result = model.processContent(content)
      result should not be null
    }
    
    "create new model with WebScraperModelCreate" in {
      val model = WebScraperModel(List("old"))
      val observer = new TestObserver
      model.addObserver(observer)
      
      val newModel = model.WebScraperModelCreate(List("new"))
      newModel.currentContent shouldBe List("new")
      newModel.getObservers() should contain(observer)
    }
    
    "notify observers when processing content" in {
      val model = WebScraperModel(Nil)
      val observer = new TestObserver
      model.addObserver(observer)
      
      val content = new UserInputTyp("test")
      model.processContent(content)
      
      observer.updates should not be empty
    }
  }
}