package org.jglr.phiengine.client.input

import org.lwjgl.glfw._

abstract class InputProcessor extends InputListener {
  private var currentMouseX: Int = 0
  private var currentMouseY: Int = 0

  var cursorPosCallback: GLFWCursorPosCallback = new GLFWCursorPosCallback() {
    def invoke(window: Long, xpos: Double, ypos: Double) {
      if (currentMouseX != xpos.toInt || currentMouseY != ypos.toInt) {
        currentMouseX = xpos.toInt
        currentMouseY = ypos.toInt
        onMouseMoved(currentMouseX, currentMouseY)
      }
    }
  }

  var mouseButtonCallback: GLFWMouseButtonCallback = new GLFWMouseButtonCallback() {
    def invoke(window: Long, button: Int, action: Int, mods: Int) {
      if (action == GLFW.GLFW_PRESS) onMousePressed(currentMouseX, currentMouseY, button)
      else if (action == GLFW.GLFW_RELEASE) onMouseReleased(currentMouseX, currentMouseY, button)
    }
  }

  var scrollCallback: GLFWScrollCallback = new GLFWScrollCallback() {
    def invoke(window: Long, xoffset: Double, yoffset: Double) {
      onScroll(Math.signum(yoffset).toInt)
    }
  }

  var keyCallback: GLFWKeyCallback = new GLFWKeyCallback() {
    def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
      if (action == GLFW.GLFW_PRESS) onKeyPressed(key)
      else if (action == GLFW.GLFW_RELEASE) onKeyReleased(key)
    }
  }

  var charCallback: GLFWCharCallback = new GLFWCharCallback() {
    def invoke(window: Long, codepoint: Int) {
      val c: Char = Character.toChars(codepoint)(0)
      onKeyTyped(c)
    }
  }

}