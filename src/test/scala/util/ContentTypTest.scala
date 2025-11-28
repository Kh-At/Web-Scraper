package de.htwg.se

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import java.nio.file.{Files, Paths}

class ContentTypTest extends AnyWordSpec {

  "ContentTyp implementations" should {
    
    "UserInputTyp return correct content and source type" in {
      val source = new UserInputTyp("test input")
      source.getContent() should be(List("test input"))
      source.getSourceType() should be("user input")
    }
    
    "UserInputTyp handle empty input" in {
      val source = new UserInputTyp("")
      source.getContent() should be(List(""))
      source.getSourceType() should be("user input")
    }
    
    "UserInputTyp handle multi-line input" in {
      val source = new UserInputTyp("line1\nline2\nline3")
      source.getContent() should be(List("line1\nline2\nline3"))
    }
    
    "FileContentTyp load file content correctly" in {
      val testFile = "test_input.txt"
      Files.write(Paths.get(testFile), "line1\nline2\nline3".getBytes)
      
      val source = new FileContentTyp(testFile)
      source.getContent() should be(List("line1", "line2", "line3"))
      source.getSourceType() should be("file")
      
      Files.deleteIfExists(Paths.get(testFile))
    }
    
    "FileContentTyp handle empty file" in {
      val testFile = "empty_test.txt"
      Files.write(Paths.get(testFile), "".getBytes)
      
      val source = new FileContentTyp(testFile)
      source.getContent() should be(List())
      source.getSourceType() should be("file")
      
      Files.deleteIfExists(Paths.get(testFile))
    }
    
    "FileContentTyp throw exception for non-existent file" in {
      val source = new FileContentTyp("nonexistent_file_12345.txt")
      an[Exception] should be thrownBy source.getContent()
    }
    
    "WebsiteContentTyp return placeholder content" in {
      val source = new WebsiteContentTyp("http://example.com")
      source.getContent() should be(List("Website: http://example.com", "Content would be scraped here..."))
      source.getSourceType() should be("website")
    }
    
    "WebsiteContentTyp handle different URLs" in {
      val source1 = new WebsiteContentTyp("https://google.com")
      val source2 = new WebsiteContentTyp("https://github.com")
      
      source1.getContent() should be(List("Website: https://google.com", "Content would be scraped here..."))
      source2.getContent() should be(List("Website: https://github.com", "Content would be scraped here..."))
      source1.getSourceType() should be("website")
      source2.getSourceType() should be("website")
    }
  }
}