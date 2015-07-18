package org.jglr.phiengine.client.input

import java.util

import com.google.common.collect.Maps
import org.lwjgl.opengl.GL11
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.{Map, HashMap}
import org.lwjgl.glfw.GLFW._
import scala.collection.JavaConversions._

class Controller(val id: Int = 0) {
  private final val buttons: Map[Int, Boolean] = new HashMap[Int, Boolean]
  private final val axes: Map[Int, Float] = new HashMap[Int, Float]
  private var name: String = null
  private var listener: ControllerListener = null
  private var connected: Boolean = false

  if (id > GLFW_JOYSTICK_LAST) {
    throw new IllegalArgumentException("Cannot specify an id greater than " + GLFW_JOYSTICK_LAST + ", got " + id)
  }

  def setListener(listener: ControllerListener) {
    this.listener = listener
  }

  def getListener: ControllerListener = {
    listener
  }

  def poll() {
    val wasConnected: Boolean = isConnected
    connected = glfwJoystickPresent(id) == GL11.GL_TRUE
    if (connected) name = glfwGetJoystickName(id)
    if (!wasConnected && isConnected) {
      fireConnected
    }
    else if (wasConnected && !isConnected) {
      fireDisconnected
    }
    if (!isConnected) {
      return
    }
    val buttons: ByteBuffer = glfwGetJoystickButtons(id)
    val axes: FloatBuffer = glfwGetJoystickAxes(id)
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

  def getAxisValue(axisIndex: Int): Float = {
    if (axes.containsKey(axisIndex)) return axes.get(axisIndex)
    return 0f
  }

  private def fireAxisMoved(axisIndex: Int, value: Float) {
    if (listener != null) listener.onAxisMoved(this, axisIndex, value)
  }

  private def fireConnected {
    if (listener != null) listener.onConnection(this)
  }

  private def fireDisconnected {
    if (listener != null) listener.onDisconnection(this)
  }

  private def fireButtonPressed(buttonIndex: Int) {
    if (listener != null) listener.onButtonPressed(this, buttonIndex)
  }

  private def fireButtonReleased(buttonIndex: Int) {
    if (listener != null) listener.onButtonReleased(this, buttonIndex)
  }

  def isButtonPressed(button: Int): Boolean = {
    return buttons.containsKey(button) && buttons.get(button)
  }

  def isConnected: Boolean = {
    return connected
  }

  def getName: String = {
    return name
  }

  def getID: Int = {
    return id
  }
}