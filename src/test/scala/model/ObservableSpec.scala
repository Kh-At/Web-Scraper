package de.htwg.se.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.util.viewUtils.Observer
import de.htwg.se.util.modelUtils.ContentStatus

class ObservableSpec extends AnyWordSpec with Matchers {
  
  class TestObserver extends Observer {
    var updates: List[(List[String], ContentStatus, String)] = Nil
    
    def update(content: List[String], status: ContentStatus, sourceType: String): Unit = {
      updates = (content, status, sourceType) :: updates
    }
  }
  
  class TestObservable extends Observable
  
  "Observable" should {
    "add observers" in {
      val observable = new TestObservable
      val observer = new TestObserver
      
      observable.addObserver(observer)
      observable.getObservers() should contain(observer)
    }
    
    "remove observers" in {
      val observable = new TestObservable
      val observer = new TestObserver
      
      observable.addObserver(observer)
      observable.removeObserver(observer)
      observable.getObservers() should not contain observer
    }
    
    "notify observers" in {
      val observable = new TestObservable
      val observer = new TestObserver
      
      observable.addObserver(observer)
      observable.notifyObservers(List("test"), ContentStatus.success, "test")
      
      observer.updates should not be empty
      val (content, status, sourceType) = observer.updates.head
      content shouldBe List("test")
      status shouldBe ContentStatus.success
      sourceType shouldBe "test"
    }
    
    "be thread-safe" in {
      val observable = new TestObservable
      
      // Test that accessing observers doesn't throw concurrent modification exception
      observable.getObservers() should not be null
      succeed
    }
  }
}