package org.jglr.phiengine.client.input

import org.jglr.phiengine.core.PhiEngine
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._

class KeyboardController(val engine: PhiEngine) extends Controller(0) {

  override def poll() {
  }

  override def isConnected: Boolean = {
    true
  }

  override def isButtonPressed(button: Int): Boolean = {
    glfwGetKey(engine.getWindow.getPointer, button) == GL_TRUE
  }

  override def getName: String = {
    "Keyboard (native: PhiEngine)"
  }
}