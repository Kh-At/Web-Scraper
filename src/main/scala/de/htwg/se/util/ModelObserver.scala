// ModelObserver.scala
package de.htwg.se

trait ModelObserver {
  def onModelChanged(content: List[String], status: String, sourceType: String): Unit
}