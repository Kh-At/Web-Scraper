package de.htwg.se

import scala.io.Source
import scala.io.StdIn.readLine
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.util.{Using, Try, Success, Failure}

class Tui (x:Int, y:Int) extends Observer {
  val pipe: String = "|"
  val plus: String = "+"
  val minus: String = "-"
  val space: String = " "

  def build_bar(x:Int): String = plus + minus * x + plus + "\n"
  def build_tower(x:Int, y:Int): String = (0 until y).map(i =>(pipe + space * x + pipe)).mkString("\n") + "\n"
  def build_all(): String = build_bar(x) + build_tower(x, y) + build_bar(x)
  def print_Tui(): Unit = println(build_all())
  
  def update: Unit = println("hi");
  
  var inputFileName: String = "";
  def getInuptFileName() : String = inputFileName
  def ioToFile(userInput: String, outputFile: String): Unit  = { // Reads the user input and puts it into a file.
    inputFileName = outputFile
    Using( Files.newBufferedWriter (Paths.get(outputFile), StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING)) {
        writer => writer.write(userInput + "\n")
    }
  } 
}