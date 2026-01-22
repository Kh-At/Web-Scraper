package de.htwg.se.model

import de.htwg.se.util.modelUtils.contentTyp._

trait ScraperModelInterface extends Observable {
  def currentContent: List[String]
  def getCurrentContent(): List[String]
  def processContent(source: ContentTyp): WebScraperModel
  def WebScraperModelCreate(newContent: List[String]): WebScraperModel
}