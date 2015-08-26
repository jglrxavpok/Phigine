package org.jglr.phiengine.client

import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._

class WindowPointer(val pointer: Long) {
  def setSize(width: Int, height: Int) = {
    glfwSetWindowSize(pointer, width, height)
  }


  def destroy {
    glfwDestroyWindow(pointer)
  }

  def show {
    glfwShowWindow(pointer)
  }

  def hide {
    glfwHideWindow(pointer)
  }

  def setPos(x: Int, y: Int) {
    glfwSetWindowPos(pointer, x, y)
  }

  def shouldClose: Boolean = {
    glfwWindowShouldClose(pointer) == GL_TRUE
  }

  def pollEvents {
    glfwPollEvents()
  }

  def getPointer: Long = pointer

  def swapBuffers {
    glfwSwapBuffers(pointer)
  }
}