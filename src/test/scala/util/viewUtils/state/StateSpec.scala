package de.htwg.se.util.viewUtils.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class StateSpec extends AnyWordSpec with Matchers {
  
  "On state" should {
    "have true value" in {
      val onState = On()
      
      onState.getValue shouldBe true
    }
  }
  
  "Off state" should {
    "have false value" in {
      val offState = Off()
      
      offState.getValue shouldBe false
    }
  }
  
  "Context" should {
    "store current state" in {
      val onState = On()
      val context = Context(onState)
      
      context.getState shouldBe onState
      context.getStateValue shouldBe true
    }
    
    "set new state" in {
      val onState = On()
      val offState = Off()
      val context = Context(onState)
      
      val newContext = context.setState(offState)
      newContext.getState shouldBe offState
      newContext.getStateValue shouldBe false
      
      // Original context should not be modified
      context.getState shouldBe onState
    }
  }
}