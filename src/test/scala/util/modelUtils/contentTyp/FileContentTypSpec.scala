package de.htwg.se.util.modelUtils.contentTyp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.util.FileIO

class FileContentTypSpec extends AnyWordSpec with Matchers {
  
  class MockFileIO extends FileIO {
    def save(filename: String, sitename: String, content: String): Boolean = true
    
    def load(filename: String): List[String] = {
      if (filename == "existing.txt") List("line1", "line2")
      else throw new RuntimeException("File not found")
    }
  }
  
  "FileContentTyp" should {
    "load content from existing file" in {
      given fileIO: FileIO = new MockFileIO
      val fileContent = new FileContentTyp("existing.txt")
      
      fileContent.getSourceType() shouldBe "file"
      fileContent.getContent() shouldBe List("line1", "line2")
    }
    
    "handle non-existent file" in {
      given fileIO: FileIO = new MockFileIO
      val fileContent = new FileContentTyp("nonexistent.txt")
      
      fileContent.getContent() should contain ("File not found!")
    }
  }
}