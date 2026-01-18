package de.htwg.se.util.viewUtils

class Messages {
    private val welcomeMessage: String = s"Web Scraper TUI" + "\n" + "Tippe 'help' für verfügbare Befehle"
    private val nothingToUndo: String = "Nothing to undo"
    private val nothingToRedo: String = "Nothing to redo"
    private val byby: String = "auf wiedersehen!"
    private val helpMessage: String = 
        "|Verfügbare Befehle:" + "\n"
        + "|  load <dateiname>    - Lädt Inhalt aus Datei " + "\n"
        + "|  scrape <url>        - Scraped Inhalt von Website" + "\n"
        + "|  input oder i        - Startet Input-Modus für mehrzeiligen Text" + "\n"
        + "|  input <text>        - Verarbeitet direkte Texteingabe (einzeilig)" + "\n"
        + "|  save <dateiname>    - Speichert aktuellen Content" + "\n"
        + "|  clear               - Leert den Content" + "\n"
        + "|  help                - Zeigt diese Hilfe" + "\n"
        + "|  exit/quit           - Beendet das Programm" + "\n"

    def getWelcomeMessage(): String = welcomeMessage
    def getHelpmessage(): String = helpMessage
    def getNothingToUndo(): String = nothingToUndo
    def getNothingToRedo(): String = nothingToRedo
    def getByby(): String = byby
}