package de.htwg.se.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.util.modelUtils.contentTyp._

class ScraperModelInterfaceSpec extends AnyWordSpec with Matchers {
  
  class MockScraperModel extends ScraperModelInterface {
    var mockContent: List[String] = List("test")
    
    def currentContent: List[String] = mockContent
    def getCurrentContent(): List[String] = mockContent
    
    def processContent(source: ContentTyp): WebScraperModel = {
      WebScraperModel(source.getContent())
    }
    
    def WebScraperModelCreate(newContent: List[String]): WebScraperModel = {
      WebScraperModel(newContent)
    }
  }
  
  "ScraperModelInterface" should {
    "define required methods" in {
      val model = new MockScraperModel
      
      model.currentContent shouldBe List("test")
      model.getCurrentContent() shouldBe List("test")
      
      val content = new UserInputTyp("test input")
      val result = model.processContent(content)
      result should not be null
      
      val newModel = model.WebScraperModelCreate(List("new"))
      newModel should not be null
    }
  }
}