package org.jglr.phiengine.client.input

import org.jglr.phiengine.core.PhiEngine
import java.util.ArrayList
import java.util.List

class InputHandler(val engine: PhiEngine) extends InputProcessor {
  private final val keys: List[Input] = new ArrayList[Input]
  private final val mouseButtons: List[Input] = new ArrayList[Input]
  private final val mouseMoveListeners: List[Input] = new ArrayList[Input]

  private val keyboardController = new KeyboardController(engine)

  private def update(id: Int, list: List[Input], value: Float, isPressed: Boolean): Boolean = {
    import scala.collection.JavaConversions._
    for (input <- list) {
      if (input.getId == id) {
        input.isPressed = isPressed
        input.setValue(value)
        return true
      }
    }
    false
  }

  def onKeyPressed(keycode: Int): Boolean = {
    update(keycode, keys, 1f, true)
  }

  def onKeyReleased(keycode: Int): Boolean = {
    update(keycode, keys, 0f, false)
  }

  def onKeyTyped(character: Char): Boolean = {
    false
  }

  def onMousePressed(screenX: Int, screenY: Int, button: Int): Boolean = {
    update(button, mouseButtons, 1f, true)
  }

  def onMouseReleased(screenX: Int, screenY: Int, button: Int): Boolean = {
    update(button, mouseButtons, 0f, false)
  }

  def onMouseMoved(screenX: Int, screenY: Int): Boolean = {
    var result: Boolean = update(Input.MOUSE_MOVE_X, mouseMoveListeners, screenX, false)
    result |= update(Input.MOUSE_MOVE_Y, mouseMoveListeners, screenY, false)
    result
  }

  def onScroll(dir: Int): Boolean = {
    false
  }

  def getKeys: List[Input] = {
    keys
  }

  def getMouseButtons: List[Input] = {
    mouseButtons
  }

  def getMouseMoveListeners: List[Input] = {
    mouseMoveListeners
  }

  def createMouseMoveListener(id: Int, name: String): Input = {
    createInput(id, name, Input.Type.MOUSE)
  }

  def createMouseButton(id: Int, name: String): Input = {
    createInput(id, name, Input.Type.MOUSE_BUTTON)
  }

  def createKey(id: Int, name: String): Input = {
    createInput(id, name, Input.Type.KEY)
  }

  private def createInput(id: Int, name: String, inputType: Input.Type.Type): Input = {
    val input = new Input(id, keyboardController, name, inputType)
    inputType match {
      case Input.Type.MOUSE_BUTTON =>
        mouseButtons add input

      case Input.Type.MOUSE =>
        mouseMoveListeners add input

      case Input.Type.KEY =>
        keys add input

      case _ =>
    }
    input
  }
}