package de.htwg.se

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import java.nio.file.{Files, Paths}

class WebScraperModelTest extends AnyWordSpec {

  class TestObserver extends ModelObserver {
    var lastContent: List[String] = List()
    var lastStatus: String = ""
    var lastSourceType: String = ""
    var callCount: Int = 0
    
    def update(content: List[String], status: String, sourceType: String): Unit = {
      lastContent = content
      lastStatus = status
      lastSourceType = sourceType
      callCount += 1
    }
    
    def reset(): Unit = {
      lastContent = List()
      lastStatus = ""
      lastSourceType = ""
      callCount = 0
    }
  }

  "WebScraperModel" should {
    val model = new WebScraperModel()
    val testObserver = new TestObserver()
    
    "initialize with empty content and ready status" in {
      model.getContent should be(List())
      model.getStatus should be("ready")
      model.getSourceType should be("none")
    }
    
    "add and remove observers correctly" in {
      model.addObserver(testObserver)
      model.removeObserver(testObserver)
      // Should not throw any exceptions
    }
    
    "process user input content successfully" in {
      model.addObserver(testObserver)
      testObserver.reset()
      
      val source = new UserInputTyp("test content")
      model.processContent(source)
      
      model.getContent should be(List("test content"))
      model.getStatus should be("success")
      model.getSourceType should be("user input")
      testObserver.callCount should be(1)
      
      model.removeObserver(testObserver)
    }
    
    "process file content successfully" in {
      model.addObserver(testObserver)
      testObserver.reset()
      
      val testFile = "test_model.txt"
      Files.write(Paths.get(testFile), "model test content".getBytes)
      
      val source = new FileContentTyp(testFile)
      model.processContent(source)
      
      model.getContent should contain ("model test content")
      model.getStatus should be("success")
      model.getSourceType should be("file")
      
      Files.deleteIfExists(Paths.get(testFile))
      model.removeObserver(testObserver)
    }
    
    "process website content successfully" in {
      model.addObserver(testObserver)
      testObserver.reset()
      
      val source = new WebsiteContentTyp("http://test.com")
      model.processContent(source)
      
      model.getContent should be(List("Website: http://test.com", "Content would be scraped here..."))
      model.getStatus should be("success")
      model.getSourceType should be("website")
      testObserver.callCount should be(2) // Called twice for website (loading + success)
      
      model.removeObserver(testObserver)
    }
    
    "MessageTyp handle farewell message" in {
      val source = new MessageTyp("auf wiedersehen!")
      source.getContent() should be(List("auf wiedersehen!"))
      source.getSourceType() should be("Message")
    }

    "handle errors gracefully when file not found" in {
      model.addObserver(testObserver)
      testObserver.reset()
      
      val invalidSource = new FileContentTyp("nonexistent_file.txt")
      model.processContent(invalidSource)
      
      model.getStatus should be("error")
      model.getContent.head should startWith ("Error:")
      testObserver.callCount should be(1)
      
      model.removeObserver(testObserver)
    }
    
    "notify multiple observers" in {
      val observer1 = new TestObserver()
      val observer2 = new TestObserver()
      
      model.addObserver(observer1)
      model.addObserver(observer2)
      
      model.processContent(new UserInputTyp("multi observer test"))
      
      observer1.lastContent should be(List("multi observer test"))
      observer2.lastContent should be(List("multi observer test"))
      observer1.lastStatus should be("success")
      observer2.lastStatus should be("success")
      
      model.removeObserver(observer1)
      model.removeObserver(observer2)
    }
    
    "not notify removed observers" in {
      val testObserver = new TestObserver()
      
      model.addObserver(testObserver)
      model.removeObserver(testObserver)
      model.processContent(new UserInputTyp("test"))
      
      testObserver.lastContent should be(List())
      testObserver.callCount should be(0)
    }
  }
}