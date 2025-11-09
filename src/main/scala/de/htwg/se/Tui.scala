package de.htwg.se
class Tui (x:Int, y:Int) {
  val pipe:String = "|"
  val plus:String = "+"
  val minus:String = "-"
  val space:String = " "

  def build_bar(x:Int): String = plus + minus * x + plus + "\n"

  def build_tower(x:Int, y:Int): String = (0 until y).map(i => pipe + space * x + pipe).mkString("\n") + "\n"
  
  def build_all(): String = build_bar(x) + build_tower(x, y) + build_bar(x)
  
  def print_Tui(): Unit = println(build_all())

}