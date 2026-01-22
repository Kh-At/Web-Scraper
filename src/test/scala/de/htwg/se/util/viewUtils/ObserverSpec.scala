package de.htwg.se.util.viewUtils

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.util.modelUtils.ContentStatus

class ObserverSpec extends AnyWordSpec with Matchers {
  
  class TestObserver extends Observer {
    var lastUpdate: Option[(List[String], ContentStatus, String)] = None
    
    def update(content: List[String], status: ContentStatus, sourceType: String): Unit = {
      lastUpdate = Some((content, status, sourceType))
    }
  }
  
  "Observer" should {
    "define update method" in {
      val observer = new TestObserver
      
      observer.update(List("test"), ContentStatus.success, "test")
      observer.lastUpdate shouldBe Some((List("test"), ContentStatus.success, "test"))
    }
  }
}