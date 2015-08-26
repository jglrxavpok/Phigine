package org.jglr.phiengine.client.input

object Input {
  val MOUSE_MOVE_X: Int = 42
  val MOUSE_MOVE_Y: Int = -42

  object Type extends Enumeration {
    type Type = Value
    val MOUSE_BUTTON, MOUSE, AXIS, POV, GAMEPAD_BUTTON, KEY = Value
  }

}

class Input(private val id: Int, private val controller: Controller, private val name: String, private val inputType: Input.Type.Type) {

  private var threshold: Float = 0
  var isPressed: Boolean = false
  private var value: Float = 0
  var isTrigger = false
  def setThreshold(threshold: Float): Input = {
    this.threshold = threshold
    this
  }

  def getController: Controller = {
    controller
  }

  def getThreshold = threshold

  def getName: String = {
    name
  }

  def getId: Int = {
    id
  }

  def getType: Input.Type.Type = {
    inputType
  }

  def setValue(value: Float): Input = {
    if (Math.abs(value) < threshold)
      this.value = 0f
    else
      this.value = value
    this
  }

  def getValue: Float = {
    value
  }

  def setTriggerLike(): Input = {
    isTrigger = true
    this
  }
}