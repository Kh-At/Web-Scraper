package de.htwg.se

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class ModelObserverTest extends AnyWordSpec {

  "ModelObserver" should {
    
    "be implemented by Tui correctly" in {
      val tui = new Tui(10, 5)
      
      // Test that Tui implements ModelObserver interface
      tui.update(List("test"), "success", "user_input")
      
      // The method should execute without errors
      // We can't test private display methods
    }
    
    "allow custom implementations" in {
      class TestObserver extends ModelObserver {
        var receivedContent: List[String] = List()
        var receivedStatus: String = ""
        var receivedSourceType: String = ""
        
        def update(content: List[String], status: String, sourceType: String): Unit = {
          receivedContent = content
          receivedStatus = status
          receivedSourceType = sourceType
        }
      }
      
      val observer = new TestObserver()
      val model = new WebScraperModel()
      
      model.addObserver(observer)
      model.processContent(new UserInputTyp("observer test"))
      
      observer.receivedContent should be(List("observer test"))
      observer.receivedStatus should be("success")
      observer.receivedSourceType should be("user input")
    }
  }
}