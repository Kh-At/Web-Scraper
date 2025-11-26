package de.htwg.se
import scala.collection.mutable.ListBuffer

class WebScraperModel {
  private var content: List[String] = List()
  private var status: String = "ready"
  private var sourceType: String = "none"
  
  private val observers: ListBuffer[ModelObserver] = ListBuffer()
  private val welcomeMessage = s"Web Scraper TUI" + "\n" + "Tippe 'help' für verfügbare Befehle"
  private val helpMessage:String = 
      "|Verfügbare Befehle:" + "\n"
    + "|  load <dateiname>    - Lädt Inhalt aus Datei " + "\n"
    + "|  scrape <url>        - Scraped Inhalt von Website" + "\n"
    + "|  input oder i        - Startet Input-Modus für mehrzeiligen Text" + "\n"
    + "|  input <text>        - Verarbeitet direkte Texteingabe (einzeilig)" + "\n"
    + "|  save <dateiname>    - Speichert aktuellen Content" + "\n"
    + "|  clear               - Leert den Content" + "\n"
    + "|  help                - Zeigt diese Hilfe" + "\n"
    + "|  exit/quit           - Beendet das Programm" + "\n" 

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
  def getWelcomeMessage: String = welcomeMessage
  def getHelpMessage: String = helpMessage

  // Observer Pattern Methoden
  def addObserver(observer: ModelObserver): Unit = observers += observer
  def removeObserver(observer: ModelObserver): Unit = observers -= observer
  private def notifyObservers(): Unit = {
    observers.foreach(_.update(content, status, sourceType))
  }
}