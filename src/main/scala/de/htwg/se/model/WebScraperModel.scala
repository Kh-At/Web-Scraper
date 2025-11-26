package de.htwg.se
import scala.collection.mutable.ListBuffer

class WebScraperModel {
  private var content: List[String] = List()
  private var status: String = "ready"
  private var sourceType: String = "none"
  private val observers: ListBuffer[ModelObserver] = ListBuffer()

  def processContent(source: ContentTyp): Unit = {
    this.status = "loading"
    this.sourceType = source.getSourceType()
    
    source match {
        case _: WebsiteContentTyp => notifyObservers()
        case _ =>
    }
    
    try {
      this.content = source.getContent()
      this.status = "success"
    } catch {
      case e: Exception =>
        this.content = List(s"Error: ${e.getMessage}")
        this.status = "error"
    }
    notifyObservers()
  }

  def getContent: List[String] = content
  def getStatus: String = status
  def getSourceType: String = sourceType

  // Observer Pattern Methoden
  def addObserver(observer: ModelObserver): Unit = observers += observer
  def removeObserver(observer: ModelObserver): Unit = observers -= observer
  private def notifyObservers(): Unit = {
    observers.foreach(_.update(content, status, sourceType))
  }
}