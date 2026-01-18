package de.htwg.se.view

import scalafx.geometry._
import scalafx.scene.Scene
import scalafx.scene.layout._
import scalafx.scene.control._
import scalafx.application.JFXApp3
import de.htwg.se.util.viewUtils.Observer
import de.htwg.se.util.modelUtils.ContentStatus
import de.htwg.se.controller.ControllerInterface

class Gui (using controller: ControllerInterface) extends JFXApp3 with Observer {

  private var inputField: TextField = _
  private var outputArea: TextArea = _

  override def start(): Unit = {
    val helpBtn   = new Button("Help")
    val loadBtn   = new Button("Load")
    val saveBtn   = new Button("Save")
    val scrapeBtn = new Button("Scrape")
    val clearBtn  = new Button("Clear")
    val exitBtn   = new Button("Exit")

    outputArea = new TextArea {
      editable = false
      wrapText = true
      prefHeight = 200
    }

    inputField = new TextField {
      promptText = "Eingabe..."
      onAction = _ => {
        controller.Inputhandler("input " + inputField.text.value)
      }
    }

    helpBtn.onAction = _ => controller.Inputhandler("help")
    loadBtn.onAction = _ => controller.Inputhandler("load " + inputField.text.value)
    saveBtn.onAction = _ => controller.Inputhandler("save " + inputField.text.value)
    scrapeBtn.onAction = _ => controller.Inputhandler("scrape " + inputField.text.value)
    clearBtn.onAction = _ => controller.Inputhandler("clear")
    exitBtn.onAction = _ => {controller.Inputhandler("exit"); System.exit(1)}

    stage = new JFXApp3.PrimaryStage {
      title = "Web Crawler"
      scene = new Scene(600, 350) {
        root = new VBox(10) {
          padding = Insets(10)
          children = Seq(outputArea, inputField, new HBox(5, helpBtn, loadBtn, saveBtn, scrapeBtn, clearBtn, exitBtn)
          )
        }
      }
    }
  }

  def update(content: List[String], status: ContentStatus, sourceType: String): Unit = {
    outputArea.text = content.mkString("\n")
  }
}