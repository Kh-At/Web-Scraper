package de.htwg.se.model

import de.htwg.se.util.viewUtils.Observer
import scala.collection.mutable.ListBuffer
import de.htwg.se.util.modelUtils.ContentStatus

class Observable {
  protected val observers: ListBuffer[Observer] = ListBuffer()
  def getObservers(): ListBuffer[Observer] = this.synchronized{this.observers}
  def addObserver(observer: Observer): Unit = this.synchronized{
    observers += observer
  }
  def removeObserver(observer: Observer): Unit = this.synchronized {
    observers -= observer
  }
  def notifyObservers(content: List[String], status: ContentStatus, sourceType: String): Unit = {
      this.synchronized {observers.foreach {obs => obs.update(content, status, sourceType)}}
  }
}