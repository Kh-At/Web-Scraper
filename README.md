[![Coverage Status](https://coveralls.io/repos/github/Kh-At/Web-Scraper/badge.svg?branch=master)](https://coveralls.io/github/Kh-At/Web-Scraper?branch=master)

# Web Scraper Projekt

**Beschreibung:**  
Dieses Projekt ist ein Web Scraper, der Daten von Webseiten extrahiert und in einem strukturierten Format speichert. Es verwendet **Scala**, **SBT** (Scala Build Tool) und **Jsoup** für das Scraping. Das Projekt bietet zwei Startoptionen: eine für das Kommandozeilen-Interface (CLI) und eine für eine grafische Benutzeroberfläche (GUI).

## Features
- **Web Scraping:** Extrahiert Daten von beliebigen Webseiten.
- **Datenfilterung:** Möglichkeit, nur text zu extrahieren.
- **Speicherung der Daten:** Gesammelte Daten werden in **XML** oder **JSON** gespeichert.
- **Zwei Startoptionen:** Kommandozeilen-Interface und grafische Benutzeroberfläche (GUI).

## Technologien
- **Scala** 3.13.x
- **SBT** (Scala Build Tool) für Build-Management und Abhängigkeitsverwaltung
- **Jsoup** für das Parsen von HTML und Extrahieren von Daten
- **GUI** (optional, je nach Bedarf)

## Installation

### Voraussetzungen
- **Java 8+** muss auf deinem Rechner installiert sein (Scala benötigt Java).
- **SBT** (Scala Build Tool), um das Projekt zu bauen und auszuführen.

**Usage:**

*Start: (TUI)*
   ```scala
   sbt "run width length"
   ```
*Start: (GUI)*
   ```scala
   sbt "run width length g"
   ```
