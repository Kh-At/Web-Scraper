package de.htwg.se.config

import de.htwg.se.view._
import de.htwg.se.model._
import de.htwg.se.controller._
import de.htwg.se.util.viewUtils.Messages
import de.htwg.se.util.controllerUtils.memento._
import com.google.inject.Singleton
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import com.google.inject.{AbstractModule, Provides} 

class WebScraperModule(width: Int, height: Int) extends AbstractModule with ScalaModule {
  override def configure(): Unit = {

    bind[Messages].in[Singleton]()
    bind[MementoHistory].in[Singleton]()
    bind[ControllerInterface].to[Controller].in[Singleton]()
    bind[Gui].in[Singleton]()
    bind[Tui].in[Singleton]()
    bindConstant().annotatedWith(Names.named("width")).to(width)
    bindConstant().annotatedWith(Names.named("height")).to(height)
  }
  
  @Provides @Singleton
  def provideScraperModelInterface(): ScraperModelInterface = {
    new WebScraperModel(List(""))
  }
}