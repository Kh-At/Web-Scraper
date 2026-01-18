package de.htwg.se.util.modelUtils.contentTyp

import scala.io.Source
import scala.util.{Using, Try, Success, Failure}
import de.htwg.se.util.Downloader
import de.htwg.se.util.DocumentAdapter

trait ContentTyp {
  def getSourceType(): String
  def getContent(): List[String]
}

class FileContentTyp(filename: String) extends ContentTyp {
  def getSourceType(): String = "file"
  def getContent(): List[String] = {
    val result: Try[List[String]] = Using(Source.fromFile(filename)) {src => src.getLines().toList}
    result match {
      case Success(lines) => lines 
      case Failure(ex) => Nil
    }
  }
}

class WebsiteContentTyp(url: String) extends ContentTyp {
  def getSourceType(): String = "website"
  def getContent(): List[String] = {
  val result: Try[List[String]] = Try {DocumentAdapter.from(Downloader.request(url)).htmlToPlainText().split("\n").toList}
    result match {
      case Success(lines) => lines 
      case Failure(ex) => Nil
    }
  }
}

class UserInputTyp(input: String) extends ContentTyp {
  def getSourceType(): String = "user input"
  def getContent(): List[String] = List(input)
}

class MessageTyp(message:String) extends ContentTyp {
  def getSourceType() :String = "Message"
  def getContent(): List[String] = List(message)  
}