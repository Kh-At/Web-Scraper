// ModelObserver.scala
package de.htwg.se

trait ModelObserver {
  def update(content: List[String], status: String, sourceType: String): Unit
}