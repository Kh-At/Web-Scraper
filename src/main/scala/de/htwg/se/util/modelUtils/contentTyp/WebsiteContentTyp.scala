package de.htwg.se.util.modelUtils.contentTyp

import scala.util.{Try, Success, Failure}
import de.htwg.se.util.{Downloader, DocumentAdapter}

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