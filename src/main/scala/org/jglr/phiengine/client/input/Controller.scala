package org.jglr.phiengine.client.input

import org.lwjgl.opengl.GL11
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.{Map, HashMap}
import org.lwjgl.glfw.GLFW._

object Controllers {
  def findFirstID(start: Int = 0): Int = {
    var id = start
    var found = false
    while(id <= GLFW_JOYSTICK_LAST && !found) {
      found = glfwJoystickPresent(id) == GL11.GL_TRUE
      id+=1
    }
    if(!found)
      id = -1
    id-1
  }
}

class Controller(val id: Int = 0) {

  private final val buttons: Map[Int, Boolean] = new HashMap[Int, Boolean]
  private final val axes: Map[Int, Float] = new HashMap[Int, Float]
  private var name: String = null
  private var listener: ControllerListener = null
  private var connected: Boolean = false
  var buttonCount: Int = 0
  var axisCount: Int = 0

  if (id > GLFW_JOYSTICK_LAST) {
    throw new IllegalArgumentException("Cannot specify an id greater than " + GLFW_JOYSTICK_LAST + ", got " + id)
  }

  if(id >= 0)
    poll()

  def setListener(listener: ControllerListener) {
    this.listener = listener
  }

  def getListener: ControllerListener = {
    listener
  }

  def poll() {
    if(id < 0)
      return
    val wasConnected: Boolean = connected
    connected = glfwJoystickPresent(id) == GL11.GL_TRUE
    if (connected) name = glfwGetJoystickName(id)
    if (!wasConnected && connected) {
      fireConnected()
    }
    else if (wasConnected && !connected) {
      fireDisconnected()
    }
    if (!connected) {
      return
    }
    val buttons: ByteBuffer = glfwGetJoystickButtons(id)
    val axes: FloatBuffer = glfwGetJoystickAxes(id)
    axisCount = axes.remaining()
    buttonCount = buttons.remaining()
    var buttonIndex: Int = 0
    while (buttons.hasRemaining) {
      val state: Boolean = buttons.get == 1
      if (!isButtonPressed(buttonIndex) && state) {
        fireButtonPressed(buttonIndex)
      }
      else if (isButtonPressed(buttonIndex) && !state) {
        fireButtonReleased(buttonIndex)
      }
      this.buttons.put(buttonIndex, state)
      buttonIndex += 1
    }
    var axisIndex: Int = 0
    while (axes.hasRemaining) {
      val value: Float = axes.get
      if (getAxisValue(axisIndex) != value) {
        fireAxisMoved(axisIndex, value)
      }
      this.axes.put(axisIndex, value)
      axisIndex += 1
    }
  }

  def hasButton(i: Int): Boolean = {
    buttons.containsKey(i)
  }

  def getAxisValue(axisIndex: Int): Float = {
    if (axes.containsKey(axisIndex)) return axes.get(axisIndex)
    0f
  }

  private def fireAxisMoved(axisIndex: Int, value: Float) {
    if (listener != null) listener.onAxisMoved(this, axisIndex, value)
  }

  private def fireConnected() {
    println(s"$id connected as $name")
    if (listener != null) listener.onConnection(this)
  }

  private def fireDisconnected() {
    if (listener != null) listener.onDisconnection(this)
  }

  private def fireButtonPressed(buttonIndex: Int) {
    if (listener != null) listener.onButtonPressed(this, buttonIndex)
  }

  private def fireButtonReleased(buttonIndex: Int) {
    if (listener != null) listener.onButtonReleased(this, buttonIndex)
  }

  def isButtonPressed(button: Int): Boolean = {
    buttons.containsKey(button) && buttons.get(button)
  }

  def isConnected: Boolean = {
    connected
  }

  def getName: String = {
    name
  }

  def getID: Int = {
    id
  }

}