package de.htwg.se.util.modelUtils.contentTyp

trait ContentTyp {
  def getSourceType(): String
  def getContent(): List[String]
}