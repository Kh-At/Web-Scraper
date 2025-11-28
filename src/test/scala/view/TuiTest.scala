package de.htwg.se

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class TuiTest extends AnyWordSpec {

  "Tui" should {
    val tui = new Tui(10, 5)
    
    "build correct bar with given width" in {
      tui.build_bar(3) should be("+---+" + "\n")
      tui.build_bar(5) should be("+-----+" + "\n")
      tui.build_bar(8) should be("+--------+" + "\n")
    }
    
    "wrap long text to multiple lines" in {
      val longText = "This is a very long text that should wrap to multiple lines"
      val result = tui.build_tower(15, 10, List(longText))
      
      result.linesIterator.count(_.contains("|")) should be > 2
      result should include ("This is a very")
      result should include ("long text that")
    }
    
    "split very long words that exceed width" in {
      val longWord = "supercalifragilisticexpialidocious"
      val result = tui.build_tower(10, 5, List(longWord))
      
      result should include ("supercalif")
      result should include ("ragilistic")
      result should include ("expialidoc")
    }
    
    "format lines correctly with exact width match" in {
      val exactWidthText = "1234567890"
      val result = tui.build_tower(10, 3, List(exactWidthText))
      result should include ("1234567890")
      result should not include (" ")
    }
    
    "format lines with trailing spaces when shorter than width" in {
      val shortText = "short"
      val result = tui.build_tower(10, 3, List(shortText))
      result should include ("short     ")
    }
    
    "handle empty string in wrapText" in {
      val result = tui.wrapText("", 5)
      result should be(List(""))
    }
    
    "handle single word shorter than width in wrapText" in {
      val result = tui.wrapText("hello", 10)
      result should be(List("hello"))
    }
    
    "handle single word longer than width in wrapText" in {
      val result = tui.wrapText("verylongword", 5)
      result should be(List("veryl", "ongwo", "rd"))
    }
    
    "split long word correctly" in {
      val result = tui.splitLongWord("abcdefghij", 3)
      result should be(List("abc", "def", "ghi", "j"))
    }
    
    "show loading status correctly in build_all" in {
      val tui = new Tui(10, 5)
      tui.update(List(), "loading", "website")
      val result = tui.build_all()
      
      result should include ("Loading...")
    }

    "display method should not throw exceptions" in {
      val tui = new Tui(10, 5)
      noException should be thrownBy tui.display()
    }
  
    "be created with correct dimensions" in {
      val tui = new Tui(20, 10)
      tui should not be null
    }
    
    "update internal state via update" in {
      val tui = new Tui(15, 8)
      tui.update(List("test content"), "success", "user_input")
    }
    
    "handle different status types in update" in {
      val tui = new Tui(10, 5)
      
      tui.update(List(), "loading", "website")
      tui.update(List("error message"), "error", "file")
      tui.update(List("success content"), "success", "user_input")
      tui.update(List(), "ready", "none")
    }

    "work with model updates" in {
      val model = new WebScraperModel()
      val tui = new Tui(15, 6)
      
      model.addObserver(tui)
      model.processContent(new UserInputTyp("integration test"))
    }
}
}