package org.jglr.phiengine.client.render

import org.jglr.phiengine.core.PhiEngine
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL15._

class Mesh(verticesNumber: Int, indicesNumber: Int) {
  private final val id: Int = glGenVertexArrays
  private final val verticesBuffer: Int = glGenBuffers
  private final val indicesBuffer: Int = glGenBuffers
  private var count: Int = 0

  val vertexSize: Int = 3 + 2 + 4
  PhiEngine.getInstance().checkGLError("mesh init: creating buffers")
  glBindVertexArray(id)
  glBindBuffer(GL_ARRAY_BUFFER, verticesBuffer)
  glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(verticesNumber * vertexSize), GL_DYNAMIC_DRAW)
  PhiEngine.getInstance().checkGLError("mesh init: setting up buffers")
  glVertexAttribPointer(Shader.POS_INDEX, 3, GL_FLOAT, false, vertexSize * 4, 0)
  glEnableVertexAttribArray(Shader.POS_INDEX)
  PhiEngine.getInstance().checkGLError("mesh init: setting up attribs")
  glVertexAttribPointer(Shader.UV_INDEX, 2, GL_FLOAT, false, vertexSize * 4, 3 * 4)
  glEnableVertexAttribArray(Shader.UV_INDEX)
  PhiEngine.getInstance().checkGLError("mesh init: setting up attribs 2 ")
  glVertexAttribPointer(Shader.COLOR_INDEX, 4, GL_FLOAT, false, vertexSize * 4, 5 * 4)
  glEnableVertexAttribArray(Shader.COLOR_INDEX)
  PhiEngine.getInstance().checkGLError("mesh init: setting up attribs 3")
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer)
  glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(indicesNumber), GL_DYNAMIC_DRAW)
  PhiEngine.getInstance().checkGLError("mesh init: creating indices buffer")
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
  glBindBuffer(GL_ARRAY_BUFFER, 0)
  glBindVertexArray(0)

  def updateVertices(vertices: Array[Float], length: Int) {
    glBindBuffer(GL_ARRAY_BUFFER, verticesBuffer)
    val newBuffer: FloatBuffer = BufferUtils.createFloatBuffer(length)
    for(i <- 0 until length) {
        newBuffer.put(vertices(i))
    }
    newBuffer.flip
    glBufferSubData(GL_ARRAY_BUFFER, 0, newBuffer)
  }

  def updateIndices(indices: Array[Int], length: Int) {
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer)
    val newBuffer: IntBuffer = BufferUtils.createIntBuffer(length)
    for(i <- 0 until length) {
        newBuffer.put(indices(i))
    }
    newBuffer.flip
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, newBuffer)
    count = length
  }

  def bind() {
    glBindVertexArray(id)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer)
  }

  def render(drawingMode: Int = GL_TRIANGLES) {
    PhiEngine.getInstance().checkGLError("before rendering a mesh")
    glDrawElements(drawingMode, count, GL_UNSIGNED_INT, 0)
    PhiEngine.getInstance().checkGLError("after rendering a mesh")
  }
}