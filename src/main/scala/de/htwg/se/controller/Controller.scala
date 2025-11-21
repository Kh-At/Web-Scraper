package de.htwg.se

import scala.io.StdIn.readLine
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.util.{Using, Try}

class Controller(model: WebScraperModel, view: Tui) {
  
  private var inputMode: Boolean = false
  private var inputBuffer: List[String] = List()
  
  def start(): Unit = {
    view.showWelcome()
    view.display()
    mainLoop()
  }
  
  private def mainLoop(): Unit = {
    var running = true
    
    while (running) {
      if (!inputMode) {
        view.display() // TUI immer zuerst anzeigen
      }
      
      val prompt = if (inputMode) "input> " else "> "
      print(prompt)
      
      val input = readLine().trim
      
      if (inputMode) {
        handleInputMode(input)
      } else {
        handleNormalMode(input) match {
          case Some(continue) => running = continue
          case None => // nothing changed
        }
      }
    }
  }
  
  private def handleNormalMode(input: String): Option[Boolean] = {
    input match {
      case "exit" | "quit" =>
        println("Auf Wiedersehen!")
        Some(false)
        
      case "input" | "i" =>
        startInputMode()
        None
        
      case _ =>
        handleUserInput(input)
        None
    }
  }
  
  private def startInputMode(): Unit = {
    inputMode = true
    inputBuffer = List()
    println("\n--- Input-Modus aktiviert ---")
    println("Tippe deinen Text zeilenweise")
    println("Commands: :w = save, :q = quit, :h = help")
  }
  
  private def handleInputMode(input: String): Unit = {
    input match {
      case ":w" | ":write" =>
        saveInputBuffer()
        inputMode = false
        println("Input-Modus beendet")
        
      case ":q" | ":quit" =>
        inputMode = false
        inputBuffer = List()
        println("Input-Modus abgebrochen")
        
      case ":h" | ":help" =>
        showInputModeHelp()
        
      case "" =>
        inputBuffer = inputBuffer :+ ""
        println(s"[Leerzeile hinzugefügt] - Buffer: ${inputBuffer.size} Zeilen")
        
      case _ =>
        inputBuffer = inputBuffer :+ input
        println(s"[Zeile ${inputBuffer.size} hinzugefügt] - Buffer: ${inputBuffer.size} Zeilen")
    }
  }
  
  private def saveInputBuffer(): Unit = {
    if (inputBuffer.nonEmpty) {
      val source = new UserInputSource(inputBuffer.mkString("\n"))
      model.processContent(source)
      println(s"[${inputBuffer.size} Zeilen gespeichert]")
    } else {
      println("[Keine Daten gespeichert]")
    }
    inputBuffer = List()
  }
  
  private def showInputModeHelp(): Unit = {
    println("""
      |Input-Modus Befehle:
      |  [beliebiger Text]  - Fügt Zeile zum Buffer hinzu
      |  [leere Zeile]      - Fügt leere Zeile hinzu
      |  :w oder :write     - Speichert Buffer und beendet Modus
      |  :q oder :quit      - Bricht ab (verwirft Buffer)
      |  :h oder :help      - Zeigt diese Hilfe
    """.stripMargin.trim)
  }
  
  def handleUserInput(input: String): Unit = {
    if (input.isEmpty) return
    
    val parts = input.split("\\s+").toList
    
    parts match {
      case "help" :: Nil =>
        showHelp()
        
      case "load" :: filename :: Nil =>
        processFileInput(filename)
        
      case "scrape" :: url :: Nil =>
        processWebsiteInput(url)
        
      case "input" :: text => 
        processUserInput(text.mkString(" "))
        
      case "save" :: filename :: Nil =>
        saveCurrentContent(filename)
        
      case "clear" :: Nil =>
        model.processContent(new UserInputSource(""))
        println("[Content cleared]")
        
      case _ =>
        println(s"Unbekannter Befehl: '$input'")
        showHelp()
    }
  }
  
  private def processFileInput(filename: String): Unit = {
    Try {
      val source = new FileContentSource(filename)
      model.processContent(source)
    }.recover {
      case e: Exception =>
        println(s"Fehler beim Laden: ${e.getMessage}")
    }
  }
  
  private def processWebsiteInput(url: String): Unit = {
    val source = new WebsiteContentSource(url)
    model.processContent(source)
  }
  
  private def processUserInput(text: String): Unit = {
    val source = new UserInputSource(text)
    model.processContent(source)
  }
  
  private def saveCurrentContent(filename: String): Unit = {
    val content = model.getContent.mkString("\n")
    Try {
      Using(Files.newBufferedWriter(Paths.get(filename), 
             StandardOpenOption.CREATE, 
             StandardOpenOption.TRUNCATE_EXISTING)) { writer =>
        writer.write(content + "\n")
      }
      println(s"Content gespeichert in: $filename")
    }.recover {
      case e: Exception =>
        println(s"Fehler beim Speichern: ${e.getMessage}")
    }
  }
  
  private def showHelp(): Unit = {
    println("""
      |Verfügbare Befehle:
      |  load <dateiname>    - Lädt Inhalt aus Datei
      |  scrape <url>        - Scraped Inhalt von Website  
      |  input oder i        - Startet Input-Modus für mehrzeiligen Text
      |  input <text>        - Verarbeitet direkte Texteingabe (einzeilig)
      |  save <dateiname>    - Speichert aktuellen Content
      |  clear               - Leert den Content
      |  help                - Zeigt diese Hilfe
      |  exit/quit           - Beendet das Programm
    """.stripMargin.trim)
  }
}