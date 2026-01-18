package de.htwg.se.main

import de.htwg.se.view.Tui
import de.htwg.se.view.Gui
import de.htwg.se.model.WebScraperModel
import de.htwg.se.model.ScraperModelInterface
import de.htwg.se.controller.Controller
import de.htwg.se.controller.ControllerInterface
import de.htwg.se.util.viewUtils.Messages
import de.htwg.se.util.controllerUtils.memento.MementoHistory

object Main {
  def main(args: Array[String]): Unit = {
    if (!checkArgs(args)) System.exit(1)

    val firstParameter: Int = args(0).toInt
    val secondParameter: Int = args(1).toInt

    given messages: Messages = Messages()
    given memento: MementoHistory = MementoHistory()
    given model: ScraperModelInterface = WebScraperModel(Nil)
    given controller: ControllerInterface = Controller(using summon[ScraperModelInterface], summon[Messages], summon[MementoHistory])
    given tui: Tui = Tui(width = firstParameter, height = secondParameter) (using summon[ControllerInterface])
    given gui: Gui = Gui(using summon[ControllerInterface])

    model.addObserver(gui)
    model.addObserver(tui)
    
    val tuiThread = new Thread(new Runnable {
      override def run(): Unit = tui.start()
      })
    tuiThread.setName("TUI-Thread")
    tuiThread.start()

    val guiThread = new Thread(new Runnable {
      override def run(): Unit = gui.main(Array())
      })
    guiThread.setName("GUI-Thread")
    guiThread.start()
  }

  def checkArgs(argumentsToCheck: Array[String]): Boolean = {
    if (argumentsToCheck.length < 2) return false
    try {
      argumentsToCheck(0).toInt >= 1 && argumentsToCheck(1).toInt >= 1
    } catch {
      case _: NumberFormatException => false
    }
  }
}