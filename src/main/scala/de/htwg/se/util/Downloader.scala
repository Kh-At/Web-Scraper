package de.htwg.se.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object Downloader{
    def request(site: String): Document = {
        val htmlDoc: Document = Jsoup.connect(site).userAgent("MyBot").timeout(10000).get()
        htmlDoc
    }

    def htmlToPlainText(doc: Document): String = {

        doc.select("h1, h2, h3, h4, h5, h6").forEach { e =>
            val level = e.tagName().substring(1).toInt
            e.prependText("#" * level + " ")
            e.before("\n\n")
            e.after("\n\n")
        }

        doc.select("li").forEach { e => e.prependText("- "); e.before("\n"); e.after("\n");}
        doc.select("br").forEach(_.after("\n"))
        doc.select("p, div").forEach(_.after("\n\n"))

        doc.select("a[href]").forEach { e =>
            val href = e.attr("abs:href")
            if (href.nonEmpty) e.text(s"${e.text()} ($href)")
        }

        val rawText = doc.text().replaceAll("[ \\t]+", " ").replaceAll("\\n{3,}", "\n\n").trim
        rawText
    }
}