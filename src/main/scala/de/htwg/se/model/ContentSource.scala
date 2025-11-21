package de.htwg.se
import scala.io.Source

trait ContentSource {
  def getContent(): List[String]
  def getSourceType(): String
}

class FileContentSource(filename: String) extends ContentSource {
  def getContent(): List[String] = Source.fromFile(filename).getLines().toList
  def getSourceType(): String = "file"
}

class UserInputSource(input: String) extends ContentSource {
  def getContent(): List[String] = List(input)
  def getSourceType(): String = "user_input"
}

class WebsiteContentSource(url: String) extends ContentSource {
  def getContent(): List[String] = {
    // Platzhalter für Web-Scraping Logik
    List(s"Website: $url", "Content would be scraped here...")
  }
  def getSourceType(): String = "website"
}