package de.htwg.se

object Main {
  def main(args: Array[String]): Unit = {

    if (!checkArgs(args))
        System.exit(1)

    val width: Int= args(0).toInt
    val height: Int= args(1).toInt
    val model = new WebScraperModel()
    val view = new Tui(width, height) // Der Erste Parameter ist die Breite und der Zweite ist die Höhe.
    val controller = new Controller(model, view)
      
    model.addObserver(view)
    view.display()
    view.showWelcome()
    controller.start()
  }

  def checkArgs(argumentsToCheck: Array[String]): Boolean = {
    if (argumentsToCheck.length < 2) {
      println("Usage: java -jar program.jar <width> <height>" + "\n" + "Error: Missing arguments")
      false
    }

    if (argumentsToCheck(0).toInt < 1 || argumentsToCheck(1).toInt < 1) {
      println("Error: Invalid dimensions. Width and height must be at least 1.")
      false
    } else {
      true
    }
  }
}