package de.htwg.se.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.File
import scala.util.Try

class FileIOSpec extends AnyWordSpec with Matchers with org.scalatest.BeforeAndAfterEach {
  
  private var jsonFormat: JsonFormat = _
  private var xmlFormat: XMLFormat = _
  private val testFilename = "test_output.json"
  private val testXmlFilename = "test_output.xml"
  
  override def beforeEach(): Unit = {
    jsonFormat = new JsonFormat
    xmlFormat = new XMLFormat
  }
  
  override def afterEach(): Unit = {
    new File(testFilename).delete()
    new File(testXmlFilename).delete()
  }
  
  "JsonFormat" should {
    "save content to file" in {
      val result = jsonFormat.save(testFilename, "TestSite", "Test content")
      result shouldBe true
      
      val file = new File(testFilename)
      file.exists() shouldBe true
    }
    
    "load content from file" in {
      // First save some content
      jsonFormat.save(testFilename, "TestSite", "Test content")
      
      val content = jsonFormat.load(testFilename)
      content should not be empty
      content should contain allOf("TestSite", "Test content")
    }
  }
  
  "XMLFormat" should {
    "save content to file" in {
      val result = xmlFormat.save(testXmlFilename, "TestSite", "Test content")
      result shouldBe true
      
      val file = new File(testXmlFilename)
      file.exists() shouldBe true
    }
    
    "load content from file" in {
      // First save some content
      xmlFormat.save(testXmlFilename, "TestSite", "Test content")
      
      val content = xmlFormat.load(testXmlFilename)
      content should not be empty
      content should contain("TestSite")
    }
  }
  
  "FileIO trait" should {
    "be implemented by JsonFormat and XMLFormat" in {
      jsonFormat.isInstanceOf[FileIO] shouldBe true
      xmlFormat.isInstanceOf[FileIO] shouldBe true
    }
  }
}