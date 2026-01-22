package de.htwg.se.util.viewUtils.state

trait State:
  def getValue: Boolean

case class On() extends State:
  val stateValue :Boolean = true
  def getValue: Boolean = stateValue

case class Off() extends State:
  val stateValue :Boolean = false
  def getValue: Boolean = stateValue

case class Context(currentState: State):
  def getState: State = currentState
  def getStateValue: Boolean = currentState.getValue 
  def setState(newState: State): Context = this.copy(currentState = newState)