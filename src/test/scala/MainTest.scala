package de.htwg.se

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class MainTest extends AnyWordSpec {

  "Main application" should {
    
    "check arguments correctly for valid dimensions" in {
      Main.checkArgs(Array("10", "10")) should be(true)
      Main.checkArgs(Array("1", "1")) should be(true)
      Main.checkArgs(Array("100", "50")) should be(true)
    }
    
    "check arguments correctly for invalid dimensions" in {
      Main.checkArgs(Array("0", "10")) should be(false)
      Main.checkArgs(Array("10", "0")) should be(false)
      Main.checkArgs(Array("0", "0")) should be(false)
      Main.checkArgs(Array("-1", "10")) should be(false)
      Main.checkArgs(Array("10", "-5")) should be(false)
    }
    
    "check arguments correctly for non-numeric input" in {
      Main.checkArgs(Array("abc", "10")) should be(false)
      Main.checkArgs(Array("10", "xyz")) should be(false)
      Main.checkArgs(Array("abc", "xyz")) should be(false)
    }
    
    "check arguments correctly for insufficient arguments" in {
      Main.checkArgs(Array("10")) should be(false)
      Main.checkArgs(Array()) should be(false)
    }
    
    "have a working argument validation method" in {
      noException should be thrownBy Main.checkArgs(Array("5", "5"))
      noException should be thrownBy Main.checkArgs(Array("0", "0"))
      noException should be thrownBy Main.checkArgs(Array("abc", "def"))
    }
  }
}