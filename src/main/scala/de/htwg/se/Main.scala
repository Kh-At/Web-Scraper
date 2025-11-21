package de.htwg.se
object Main

def main(args:Array[String]): Unit = {
  if(checkArgs(args(0).toInt, args(1).toInt)) {
    val myTui: Tui = new Tui(args(0).toInt, args(1).toInt)
    myTui.print_Tui()
  } else
    println("Error")
}

def checkArgs( width: Int, length: Int ): Boolean = {
    if (width < 1 || length < 1) {
      false
    } else {
      true
    }
}