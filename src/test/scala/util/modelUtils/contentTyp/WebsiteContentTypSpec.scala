package de.htwg.se.util.modelUtils.contentTyp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class WebsiteContentTypSpec extends AnyWordSpec with Matchers {
  
  "WebsiteContentTyp" should {
    "have correct source type" in {
      val websiteContent = new WebsiteContentTyp("http://test.com")
      
      websiteContent.getSourceType() shouldBe "website"
    }
    
    "handle URL" in {
      val websiteContent = new WebsiteContentTyp("http://example.com")
      
      // We can't test actual web scraping, but we can test that the method exists
      websiteContent.getContent() should not be null
    }
  }
}