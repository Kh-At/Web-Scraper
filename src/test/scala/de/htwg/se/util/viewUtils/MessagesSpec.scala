package de.htwg.se.util.viewUtils

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MessagesSpec extends AnyWordSpec with Matchers {
  
  "Messages" should {
    "provide welcome message" in {
      val messages = new Messages
      
      val welcome = messages.getWelcomeMessage()
      welcome should include("Web Scraper TUI")
      welcome should include("Tippe 'help'")
    }
    
    "provide help message" in {
      val messages = new Messages
      
      val help = messages.getHelpmessage()
      help should include("Verfügbare Befehle")
      help should include("load")
      help should include("scrape")
      help should include("save")
      help should include("help")
      help should include("exit")
    }
    
    "provide nothing to undo message" in {
      val messages = new Messages
      
      messages.getNothingToUndo() shouldBe "Nothing to undo"
    }
    
    "provide nothing to redo message" in {
      val messages = new Messages
      
      messages.getNothingToRedo() shouldBe "Nothing to redo"
    }
    
    "provide goodbye message" in {
      val messages = new Messages
      
      messages.getByby() shouldBe "auf wiedersehen!"
    }
  }
}