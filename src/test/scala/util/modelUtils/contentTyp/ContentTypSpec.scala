package de.htwg.se.util.modelUtils.contentTyp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ContentTypSpec extends AnyWordSpec with Matchers {
  
  class TestContentTyp extends ContentTyp {
    def getSourceType(): String = "test"
    def getContent(): List[String] = List("test content")
  }
  
  "ContentTyp" should {
    "define required methods" in {
      val content = new TestContentTyp
      
      content.getSourceType() shouldBe "test"
      content.getContent() shouldBe List("test content")
    }
  }
}