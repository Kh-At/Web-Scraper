package de.htwg.se.util.modelUtils.contentTyp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class UserInputTypSpec extends AnyWordSpec with Matchers {
  
  "UserInputTyp" should {
    "store and return user input" in {
      val input = "Test user input"
      val inputTyp = new UserInputTyp(input)
      
      inputTyp.getSourceType() shouldBe "user input"
      inputTyp.getContent() shouldBe List(input)
    }
    
    "handle empty input" in {
      val inputTyp = new UserInputTyp("")
      
      inputTyp.getContent() shouldBe List("")
    }
  }
}