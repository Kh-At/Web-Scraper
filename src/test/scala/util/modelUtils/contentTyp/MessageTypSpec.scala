package de.htwg.se.util.modelUtils.contentTyp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MessageTypSpec extends AnyWordSpec with Matchers {
  
  "MessageTyp" should {
    "store and return message" in {
      val message = "Test message"
      val messageTyp = new MessageTyp(message)
      
      messageTyp.getSourceType() shouldBe "Message"
      messageTyp.getContent() shouldBe List(message)
    }
    
    "handle empty message" in {
      val messageTyp = new MessageTyp("")
      
      messageTyp.getContent() shouldBe List("")
    }
  }
}