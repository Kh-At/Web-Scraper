package de.htwg.se.config

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ImplicitsSpec extends AnyWordSpec with Matchers {
  "Implicits" should {
    "provide all necessary dependencies" in {
      import Implicits.given
      
      val format = summon[de.htwg.se.util.FileIO]
      val messages = summon[de.htwg.se.util.viewUtils.Messages]
      val memento = summon[de.htwg.se.util.controllerUtils.memento.MementoHistory]
      val model = summon[de.htwg.se.model.ScraperModelInterface]
      val controller = summon[de.htwg.se.controller.ControllerInterface]
      
      format should not be null
      messages should not be null
      memento should not be null
      model should not be null
      controller should not be null
    }
  }
}