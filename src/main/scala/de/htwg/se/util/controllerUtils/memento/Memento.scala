package de.htwg.se.util.controllerUtils.memento

import de.htwg.se.util.controllerUtils.commands._

case class Memento(content: List[String], command: Command) {
  def getState: Memento = this
}

class MementoHistory {
  private var undoStack: List[Memento] = List.empty
  private var redoStack: List[Memento] = List.empty

  def saveState(state: Memento): Unit = {
    undoStack = state :: undoStack
    redoStack = List.empty
  }

  def undoHistory(): Option[Memento] = undoStack match {
    case currentState :: rest =>
      undoStack = rest
      redoStack = currentState :: redoStack
      Some(currentState)
    case Nil => None
  }

  def redoHistory(): Option[Memento] = redoStack match {
    case nextState :: rest =>
      redoStack = rest
      undoStack = nextState :: undoStack
      Some(nextState)
    case Nil => None
  }

  def canUndo: Boolean = undoStack.nonEmpty
  def canRedo: Boolean = redoStack.nonEmpty
}