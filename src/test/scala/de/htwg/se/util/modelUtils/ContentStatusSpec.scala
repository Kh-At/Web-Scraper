package de.htwg.se.util.modelUtils

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ContentStatusSpec extends AnyWordSpec with Matchers {
  
  "ContentStatus" should {
    "have all expected status values" in {
      ContentStatus.ready shouldBe an[ContentStatus]
      ContentStatus.loading shouldBe an[ContentStatus]
      ContentStatus.success shouldBe an[ContentStatus]
      ContentStatus.error shouldBe an[ContentStatus]
    }
    
    "be an enumeration" in {
      val values = ContentStatus.values
      values should contain allOf(
        ContentStatus.ready,
        ContentStatus.loading,
        ContentStatus.success,
        ContentStatus.error
      )
    }
  }
}