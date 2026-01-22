package de.htwg.se.util.viewUtils

import de.htwg.se.util.modelUtils.ContentStatus

trait Observer {
  def update(content: List[String], status: ContentStatus, sourceType: String): Unit
}