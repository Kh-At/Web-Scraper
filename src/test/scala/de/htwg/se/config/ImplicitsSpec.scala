// ImplicitsSpec.scala - KORRIGIERTE VERSION
package de.htwg.se.config

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ImplicitsSpec extends AnyWordSpec with Matchers {
  "Implicits" should {
    "provide all necessary dependencies" in {
      // Importiere hier, nicht global
      import Implicits.given
      
      // Teste einzelne Implicits
      val format = summon[de.htwg.se.util.FileIO]
      format should not be null
      
      val messages = summon[de.htwg.se.util.viewUtils.Messages]
      messages should not be null
      
      val memento = summon[de.htwg.se.util.controllerUtils.memento.MementoHistory]
      memento should not be null
      
      val model = summon[de.htwg.se.model.ScraperModelInterface]
      model should not be null
      
      val controller = summon[de.htwg.se.controller.ControllerInterface]
      controller should not be null
    }
  }
}