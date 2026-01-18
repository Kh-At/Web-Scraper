package de.htwg.se.util.modelUtils.contentTyp

class UserInputTyp(input: String) extends ContentTyp {
  def getSourceType(): String = "user input"
  def getContent(): List[String] = List(input)
}