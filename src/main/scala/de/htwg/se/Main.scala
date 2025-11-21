package de.htwg.se

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println("Usage: java -jar program.jar <width> <height>")
      println("Error: Missing arguments")
      System.exit(1)
    }
    
    if (checkArgs(args(0).toInt, args(1).toInt)) {
      
      val width = args(0).toInt
      val height = args(1).toInt
      
      val model = new WebScraperModel()
      val view = new Tui(width, height)
      val controller = new Controller(model, view)
      
      model.addObserver(view)
      
      println(s"Web Scraper TUI gestartet mit Größe: ${width}x${height}")
      println("Tippe 'help' für verfügbare Befehle")
      controller.start()
      
    } else {
      println("Error: Invalid dimensions. Width and height must be at least 1.")
    }
  }

  def checkArgs(width: Int, height: Int): Boolean = {
    if (width < 1 || height < 1) {
      false
    } else {
      true
    }
  }
}