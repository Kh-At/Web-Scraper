package de.htwg.se.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.jsoup.Jsoup

class DocumentAdapterSpec extends AnyWordSpec with Matchers {
  
  "DocumentAdapter" should {
    "create from Document" in {
      val html = "<html><body><h1>Test</h1></body></html>"
      val doc = Jsoup.parse(html)
      
      val adapter = DocumentAdapter.from(doc)
      adapter should not be null
    }
    
    "convert HTML to plain text" in {
      val html = "<html><body><h1>Title</h1><p>Paragraph</p></body></html>"
      val doc = Jsoup.parse(html)
      val adapter = new DocumentAdapter(doc)
      
      val text = adapter.htmlToPlainText()
      text should include("Title")
      text should include("Paragraph")
    }
  }
}