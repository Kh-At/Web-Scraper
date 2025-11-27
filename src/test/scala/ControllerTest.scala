package de.htwg.se

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import java.nio.file.{Files, Paths}
import scala.io.Source

class ControllerTest extends AnyWordSpec {

  "Controller" should {
    val model = new WebScraperModel()
    val view = new Tui(20, 10)
    val controller = new Controller(model, view)
    
    "handle help command" in {
      noException should be thrownBy controller.handleUserInput("help")
    }
    
    "handle clear command" in {
      controller.handleUserInput("clear")
      model.getContent should be(List(""))
      model.getStatus should be("success")
    }
    
    "process file input successfully" in {
      val testFile = "test_controller.txt"
      Files.write(Paths.get(testFile), "controller test content".getBytes)
      
      controller.handleUserInput(s"load $testFile")
      model.getContent should contain ("controller test content")
      model.getStatus should be("success")
      model.getSourceType should be("file")
      
      Files.deleteIfExists(Paths.get(testFile))
    }
    
    "process direct user input" in {
      controller.handleUserInput("input direct test content")
      model.getContent should be(List("direct test content"))
      model.getStatus should be("success")
      model.getSourceType should be("user input")
    }
    
    "process multi-word user input" in {
      controller.handleUserInput("input this is a longer test with multiple words")
      model.getContent should be(List("this is a longer test with multiple words"))
    }
    
    "process website input" in {
      controller.handleUserInput("scrape http://example.com")
      model.getContent should be(List("Website: http://example.com", "Content would be scraped here..."))
      model.getStatus should be("success")
      model.getSourceType should be("website")
    }
    
    "save current content to file" in {
      model.processContent(new UserInputTyp("save test content"))
      
      val saveFile = "test_save.txt"
      controller.handleUserInput(s"save $saveFile")
      
      Files.exists(Paths.get(saveFile)) should be(true)
      val fileContent = Source.fromFile(saveFile).getLines().mkString("\n")
      fileContent should include ("save test content")
      
      Files.deleteIfExists(Paths.get(saveFile))
    }
    
    "handle unknown commands gracefully" in {
      noException should be thrownBy controller.handleUserInput("unknown command")
    }
    
    "handle empty input gracefully" in {
      noException should be thrownBy controller.handleUserInput("")
    }
    
    "handle input mode activation commands" in {
      noException should be thrownBy controller.handleUserInput("input")
      noException should be thrownBy controller.handleUserInput("i")
    }
    
    "handle file input with non-existent file gracefully" in {
      controller.handleUserInput("load nonexistent_file_123.txt")
      // Should not throw exception, model status should be error
      model.getStatus should be("error")
    }
  }
}