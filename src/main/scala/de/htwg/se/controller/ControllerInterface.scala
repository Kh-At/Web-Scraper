package de.htwg.se.controller

import scala.util.{Using, Try}
import de.htwg.se.model.{WebScraperModel, ScraperModelInterface}
import de.htwg.se.util.modelUtils.contentTyp._

trait ControllerInterface:
  def Inputhandler(input: String): Option[String]
  def saveCurrentContent(filename: String, sitename: String): Try[Unit]
  def passContent(contentToPass: ContentTyp): ScraperModelInterface