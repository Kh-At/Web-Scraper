package de.htwg.se.util.modelUtils.contentTyp

class MessageTyp(message:String) extends ContentTyp {
  def getSourceType() :String = "Message"
  def getContent(): List[String] = List(message)  
}