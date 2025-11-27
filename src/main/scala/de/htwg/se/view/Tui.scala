package de.htwg.se

class Tui(val width: Int, val height: Int) extends ModelObserver {
  val pipe: String = "|"
  val plus: String = "+"
  val minus: String = "-"
  val space: String = " "
  
  var currentContent: List[String] = List()
  var currentStatus: String = "ready"
  var currentSourceType: String = "none"

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
  
  def build_all(): String = {
    val displayContent = currentStatus match {
      case "loading" => List("Loading...")
      case "error" => currentContent
      case "success" => currentContent
      case _ => List(s"Status: $currentStatus", s"Source: $currentSourceType")
    }
    build_bar(width) + build_tower(width, height, displayContent) + build_bar(width)
  }
  
  def display(): Unit = {
    print("\n" * 5)
    println(build_all())
    println("Commands: load <file>, scrape <url>, input <text>, save <file>, help, exit")
  }

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
    val result = scala.collection.mutable.ListBuffer[String]()
    var currentLine = ""
    
    for (word <- words) {
      if (currentLine.isEmpty) {
        if (word.length <= maxWidth) {
          currentLine = word
        } else {
          result ++= splitLongWord(word, maxWidth)
          currentLine = ""
        }
      } else {
        if ((currentLine.length + 1 + word.length) <= maxWidth) {
          currentLine += " " + word
        } else {
          if (currentLine.nonEmpty) result += currentLine
          if (word.length <= maxWidth) {
            currentLine = word
          } else {
            result ++= splitLongWord(word, maxWidth)
            currentLine = ""
          }
        }
      }
    } 
    if (currentLine.nonEmpty) result += currentLine
    result.toList
  }

  def update(content: List[String], status: String, sourceType: String): Unit = {
    this.currentContent = content
    this.currentStatus = status
    this.currentSourceType = sourceType
    display()
  }
}