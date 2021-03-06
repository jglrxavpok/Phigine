package org.jglr.phiengine.client.input

import java.util.function.Predicate

import com.google.common.collect.Lists
import org.jglr.phiengine.client.ui.UI
import org.jglr.phiengine.core.PhiEngine
import org.lwjgl.glfw.GLFW
import java.util.ArrayList
import java.util.List
import scala.collection.JavaConversions._
import scala.concurrent.JavaConversions._
import org.jglr.phiengine.core.utils.JavaConversions._

class ControllerHandler(val engine: PhiEngine) extends ControllerListener {

  private var listener: ControllerListener = null
  private final val axises: List[Input] = new ArrayList[Input]
  private final val povs: List[Input] = new ArrayList[Input]
  private final val buttons: List[Input] = new ArrayList[Input]
  private final val controllers: List[Controller] = new ArrayList[Controller]

  def setListener(listener: ControllerListener) = this.listener = listener

  def onConnection(controller: Controller): Unit = {
    if(listener != null)
      listener.onConnection(controller)
  }

  def onDisconnection(controller: Controller): Unit = {
    if(listener != null)
      listener.onDisconnection(controller)
  }

  def onButtonPressed(controller: Controller, buttonCode: Int): Boolean = {
    if(listener != null)
      listener.onButtonPressed(controller, buttonCode)
    update(controller, buttonCode, buttons, 1f, true)
  }

  private def update(controller: Controller, id: Int, list: List[Input], value: Float, isPressed: Boolean): Boolean = {
    var result = false
    for (input <- list) {
      if (input.getId == id && input.getController == controller) {
        input.isPressed = isPressed
        input.setValue(value)
        if(input.isTrigger) {
          input.isPressed = Math.abs(value) > Math.abs(input.getThreshold) &&
            Math.signum(value) == Math.signum(input.getThreshold)
          if(input.isPressed) {
            input.setValue(Math.abs(value))
          } else {
            input.setValue(0f)
          }
        }
        result = true
      }
    }
    result
  }

  def onButtonReleased(controller: Controller, buttonCode: Int): Boolean = {
    if(listener != null)
      listener.onButtonReleased(controller, buttonCode)
    update(controller, buttonCode, buttons, 0f, false)
  }

  def onAxisMoved(controller: Controller, axisCode: Int, value: Float): Boolean = {
    if(listener != null)
      listener.onAxisMoved(controller, axisCode, value)
    update(controller, axisCode, axises, value, false)
  }

  def onPovMoved(controller: Controller, povCode: Int, value: PovDirection.Type): Boolean = {
    if(listener != null)
      listener.onPovMoved(controller, povCode, value)
    false
  }

  def poll() {
    controllers.foreach(c => c.poll())
  }

  def registerController(controller: Controller) {
    controller.setListener(this)
    controllers.add(controller)
  }

  def getController(id: Int): Controller = {
    controllers.stream.filter((c: Controller) => c.getID == id).findFirst.get
  }

  def getAxises: List[Input] = {
    axises
  }

  def getPovs: List[Input] = {
    povs
  }

  def getButtons: List[Input] = {
    buttons
  }

  def getAxis(controller: Controller, name: String): Input = {
    import scala.collection.JavaConversions._
    for (axis <- axises) {
      if ((name == axis.getName) && axis.getController == controller) return axis
    }
    null
  }

  def getAxis(controller: Controller, id: Int): Input = {
    for (axis <- axises) {
      if (id == axis.getId && axis.getController == controller) return axis
    }
    null
  }

  def addButton(controller: Controller, id: Int, name: String): Input = {
    val button: Input = new Input(id, controller, name, Input.Type.GAMEPAD_BUTTON)
    buttons.add(button)
    button
  }

  def addAxis(controller: Controller, id: Int, name: String): Input = {
    val axis: Input = new Input(id, controller, name, Input.Type.AXIS)
    axises.add(axis)
    axis
  }

  def addPov(controller: Controller, id: Int, name: String): Input = {
    val pov: Input = new Input(id, controller, name, Input.Type.POV)
    povs.add(pov)
    pov
  }

  def addAxis(input: Input) {
    axises.add(input)
  }

  def addButton(input: Input) {
    buttons.add(input)
  }

  def addPov(input: Input) {
    povs.add(input)
  }
}