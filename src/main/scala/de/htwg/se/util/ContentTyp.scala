package de.htwg.se
import scala.io.Source

trait ContentTyp {
  def getContent(): List[String]
  def getSourceType(): String
}

class FileContentTyp(filename: String) extends ContentTyp {
  def getContent(): List[String] = Source.fromFile(filename).getLines().toList
  def getSourceType(): String = "file"
}

class UserInputTyp(input: String) extends ContentTyp {
  def getContent(): List[String] = List(input)
  def getSourceType(): String = "user input"
}

class WebsiteContentTyp(url: String) extends ContentTyp {
  def getContent(): List[String] = {
    // Platzhalter für Web-Scraping Logik
    List(s"Website: $url", "Content would be scraped here...")
  }
  def getSourceType(): String = "website"
}

class MessageTyp(message:String) extends ContentTyp {
  def getContent(): List[String] = List(message)
  def getSourceType() :String = "Message"  
}