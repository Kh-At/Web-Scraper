package de.htwg.se.util.modelUtils.contentTyp

import scala.io.Source
import de.htwg.se.util.FileIO
import scala.util.{Using, Try, Success, Failure}

class FileContentTyp(filename: String)(using file: FileIO) extends ContentTyp {
  def getSourceType(): String = "file"
  def getContent(): List[String] = {
    val result: Try[List[String]] = Try{file.load(filename)}
    result match {
      case Success(lines) => lines
      case Failure(ex) => "fuck you!" :: Nil
    }
  }
}
