package de.htwg.se.util
import org.jsoup.nodes.Document

case class DocumentAdapter(private val doc: Document) {    
    def htmlToPlainText(): String = Downloader.htmlToPlainText(doc)
}

object DocumentAdapter {
    def from(doc: Document): DocumentAdapter =
        new DocumentAdapter(doc)
}

