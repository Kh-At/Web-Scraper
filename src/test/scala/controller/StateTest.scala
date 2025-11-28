package de.htwg.se

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StateTest extends AnyFlatSpec with Matchers {

  "State" should "be created with correct boolean value" in {
    val trueState = State(true)
    trueState.booleanValue shouldBe true
    trueState.getValue shouldBe true

    val falseState = State(false)
    falseState.booleanValue shouldBe false
    falseState.getValue shouldBe false
  }

  "setTrue" should "return new State with true value" in {
    val initialState = State(false)
    val newState = initialState.setTrue

    // Original bleibt unverändert
    initialState.booleanValue shouldBe false
    initialState.getValue shouldBe false

    // Neue Instanz hat true
    newState.booleanValue shouldBe true
    newState.getValue shouldBe true

    // Es sind verschiedene Instanzen
    initialState should not be theSameInstanceAs(newState)
  }

  "setTrue on already true state" should "return new State with true" in {
    val trueState = State(true)
    val newState = trueState.setTrue

    newState.booleanValue shouldBe true
    newState.getValue shouldBe true
    trueState should not be theSameInstanceAs(newState)
  }

  "setFalse" should "return new State with false value" in {
    val initialState = State(true)
    val newState = initialState.setFalse

    // Original bleibt unverändert
    initialState.booleanValue shouldBe true
    initialState.getValue shouldBe true

    // Neue Instanz hat false
    newState.booleanValue shouldBe false
    newState.getValue shouldBe false

    // Es sind verschiedene Instanzen
    initialState should not be theSameInstanceAs(newState)
  }

  "setFalse on already false state" should "return new State with false" in {
    val falseState = State(false)
    val newState = falseState.setFalse

    newState.booleanValue shouldBe false
    newState.getValue shouldBe false
    falseState should not be theSameInstanceAs(newState)
  }

  "Method chaining" should "work correctly" in {
    val finalState = State(false)
      .setTrue
      .setFalse
      .setTrue

    finalState.booleanValue shouldBe true
    finalState.getValue shouldBe true
  }

  "equals and hashCode" should "work correctly" in {
    val state1 = State(true)
    val state2 = State(true)
    val state3 = State(false)

    // Gleichheit
    state1 shouldEqual state2
    state1 should not equal state3

    // HashCode Konsistenz
    state1.hashCode shouldEqual state2.hashCode
    state1.hashCode should not equal state3.hashCode
  }

  "toString" should "display correctly" in {
    State(true).toString should include("true")
    State(false).toString should include("false")
  }

  "copy method" should "create modified instances" in {
    val original = State(true)
    val copiedTrue = original.copy(booleanValue = true)
    val copiedFalse = original.copy(booleanValue = false)

    original.booleanValue shouldBe true
    copiedTrue.booleanValue shouldBe true
    copiedFalse.booleanValue shouldBe false

    original should not be theSameInstanceAs(copiedTrue)
    original should not be theSameInstanceAs(copiedFalse)
  }

  "immutability" should "be preserved" in {
    val state = State(false)
    
    // Diese Aufrufe sollten das Original nicht ändern
    state.setTrue
    state.setFalse
    
    // State bleibt unverändert
    state.booleanValue shouldBe false
    state.getValue shouldBe false
  }
}