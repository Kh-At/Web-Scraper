package de.htwg.se
object Main

def main(args:Array[String]): Unit = {
    val x:Int = args(0).toInt
    val y:Int = args(1).toInt
    if (x < 1|| y < 1) {
      println("Error")
    } else {
      val myTui: Tui = new Tui(x, y)
      myTui.print_Tui()
    }
}