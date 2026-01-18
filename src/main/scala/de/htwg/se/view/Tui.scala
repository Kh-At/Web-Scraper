package de.htwg.se.view

import scala.io.StdIn.readLine
import scala.util.{Using, Try}
import de.htwg.se.util.viewUtils._
import de.htwg.se.util.viewUtils.state._
import de.htwg.se.util.modelUtils.contentTyp._
import de.htwg.se.util.modelUtils.ContentStatus
import de.htwg.se.controller.ControllerInterface

class Tui (width: Int, height: Int) (using controller: ControllerInterface)
  extends Observer {

  val pipe: String = "|"
  val plus: String = "+"
  val minus: String = "-"
  val space: String = " "

  def formatLine(line: String, contentWidth: Int): String = {
    if (line.length <= contentWidth) {
      line + space * (contentWidth - line.length)
    } else {
      line.take(contentWidth)
    }
  }

  def splitLongWord(word: String, maxWidth: Int): List[String] = {
    word.grouped(maxWidth).toList
  }

  def wrapText(text: String, maxWidth: Int): List[String] = {
    if (text.isEmpty) return List("")

    val words = text.split("\\s+").toList
    val (result, currentLine) = words.foldLeft((List.empty[String], "")) {
      case ((res, ""), word) => 
        if (word.length <= maxWidth) (res, word)
        else (res ++ splitLongWord(word, maxWidth), "")
      case ((res, line), word) =>
        if (line.length + 1 + word.length <= maxWidth) (res, line + " " + word)
        else if (line.nonEmpty) (res :+ line, word)
        else if (word.length <= maxWidth) (res, word)
        else (res ++ splitLongWord(word, maxWidth), "")
    }
    if (currentLine.nonEmpty) result :+ currentLine
    else result
  }

  def build_tower(width: Int, height: Int, content: List[String]): String = {
    val contentWidth = math.max(1, width)
    val contentHeight = math.max(1, height - 2)
    val wrappedLines = content.flatMap {line => wrapText(line, contentWidth)}
    val linesToShow = wrappedLines.take(contentHeight)
    val contentLines = linesToShow.map {line => val formattedLine = formatLine(line, contentWidth); pipe + formattedLine + pipe}
    val emptyLines = List.fill(contentHeight - linesToShow.size) (pipe + space * contentWidth + pipe )
    (contentLines ++ emptyLines).mkString("\n") + "\n"
  }

  def build_bar(width: Int): String = plus + minus * width + plus + "\n"
  def build_all(): String = build_bar(width) + build_tower(width, height, currentContent) + build_bar(width)

  def display(): Unit = {
    print("\n" * 5)
    println(build_all())
    println("Commands: load <file>, scrape <url>, input <text>, save <file>, help, exit")
  }

  var currentContent: List[String] = List()
  var currentStatus: ContentStatus = ContentStatus.ready
  var currentSourceType: String = "none"

  def update(content: List[String], status: ContentStatus, sourceType: String): Unit = {
    this.currentContent = content
    this.currentStatus = status
    this.currentSourceType = sourceType
    display()
  }

  val messages: Messages = new Messages
  val prompt = ">"

  def start(): Try[State] = {
    Try{
      controller.Inputhandler("input " + messages.getWelcomeMessage())
      mainLoop(new Context(new On))
    }
  }

  def mainLoop(state: Context): State = {
    var continueLoop = true
    while(continueLoop && state.getStateValue) {
      print(prompt)
      val input = readLine().trim
      controller.Inputhandler(input) match {
        case Some(s) => 
        case None => continueLoop = false
      }
    }
    state.getState
  }
}