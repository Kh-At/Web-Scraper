package de.htwg.se.config

import de.htwg.se.util.viewUtils.Messages
import de.htwg.se.util.{FileIO, XMLFormat, JsonFormat}
import de.htwg.se.util.controllerUtils.memento.MementoHistory
import de.htwg.se.controller.{Controller, ControllerInterface}
import de.htwg.se.model.{WebScraperModel, ScraperModelInterface}

object Implicits {
  given format: FileIO = JsonFormat()
  given messages: Messages = Messages()
  given memento: MementoHistory = MementoHistory()
  given model: ScraperModelInterface = WebScraperModel(Nil)
  given controller: ControllerInterface = Controller(using summon[ScraperModelInterface],
    summon[Messages], summon[MementoHistory])
}