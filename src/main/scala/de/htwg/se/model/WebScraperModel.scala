package de.htwg.se.model

import de.htwg.se.util.modelUtils._
import de.htwg.se.util.modelUtils.contentTyp._
import de.htwg.se.util.controllerUtils.memento._

case class WebScraperModel(val currentContent: List[String]) extends ScraperModelInterface {
  def getCurrentContent(): List[String] = currentContent
  def processContent(source: ContentTyp): WebScraperModel = {
    println("\n" + source.getContent().mkString + "\n")
    source match {
        case _: WebsiteContentTyp => notifyObservers(source.getContent(), ContentStatus.loading, source.getSourceType())
        case _ =>
    }
    notifyObservers(source.getContent(), ContentStatus.success, source.getSourceType())
    println("\n" + source.getContent().mkString + "\n")
    WebScraperModelCreate(source.getContent())
  }

  def WebScraperModelCreate(newContent: List[String]): WebScraperModel = {
    val observerCount = this.getObservers().size
    val newModel = new WebScraperModel(newContent)
    this.getObservers().foreach {observer => newModel.addObserver(observer)}
    newModel
  }
}