package org.jglr.phiengine.client.render

import org.jglr.phiengine.core.PhiEngine
import org.jglr.phiengine.core.utils.Buffers._
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL15._

object MeshUtils {
  /**
    * The size of a single vertex:<br/>
    * - 3 floats for position (x, y, z)<br/>
    * - 2 floats for texture coordinates (u, v)<br/>
    * - 3 floats for the normal (x, y, z)
    * - 4 floats for vertex color (r, g, b, a)
    */
  val vertexSize = 3 + 2 + 3 + 4
}

class Mesh(verticesNumber: Int, indicesNumber: Int) {
  private final val id: Int = glGenVertexArrays
  private final val verticesBuffer: Int = glGenBuffers
  private final val indicesBuffer: Int = glGenBuffers
  private var count: Int = 0

  private val vertexSize: Int = MeshUtils.vertexSize
  var defaultDrawMode = GL_TRIANGLES

  checkGLError("mesh init: creating buffers")
  glBindVertexArray(id)
  glBindBuffer(GL_ARRAY_BUFFER, verticesBuffer)
  glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(verticesNumber * vertexSize), GL_DYNAMIC_DRAW)
  checkGLError("mesh init: setting up buffers")
  glVertexAttribPointer(Shaders.POS_INDEX, 3, GL_FLOAT, false, vertexSize * 4, 0)
  glEnableVertexAttribArray(Shaders.POS_INDEX)

  checkGLError("mesh init: setting up attribs")
  glVertexAttribPointer(Shaders.UV_INDEX, 2, GL_FLOAT, false, vertexSize * 4, 3 * 4)
  glEnableVertexAttribArray(Shaders.UV_INDEX)

  checkGLError("mesh init: setting up attribs 2")
  glVertexAttribPointer(Shaders.NORMAL_INDEX, 3, GL_FLOAT, false, vertexSize * 4, 5 * 4)
  glEnableVertexAttribArray(Shaders.NORMAL_INDEX)

  checkGLError("mesh init: setting up attribs 3")
  glVertexAttribPointer(Shaders.COLOR_INDEX, 4, GL_FLOAT, false, vertexSize * 4, 8 * 4)
  glEnableVertexAttribArray(Shaders.COLOR_INDEX)

  checkGLError("mesh init: setting up attribs 4")
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer)
  glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(indicesNumber), GL_DYNAMIC_DRAW)
  checkGLError("mesh init: creating indices buffer")
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
  glBindBuffer(GL_ARRAY_BUFFER, 0)
  glBindVertexArray(0)

  def updateVertices(vertices: Array[Float], length: Int) {
    glBindBuffer(GL_ARRAY_BUFFER, verticesBuffer)
    glBufferSubData(GL_ARRAY_BUFFER, 0, (vertices, length))
  }

  def updateIndices(indices: Array[Int], length: Int) {
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer)
    glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, (indices, length))
    count = length
  }

  def bind() {
    glBindVertexArray(id)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer)
  }

  def render(drawingMode: Int = defaultDrawMode) {
    PhiEngine.getInstance.checkGLError("before rendering a mesh")
    glDrawElements(drawingMode, count, GL_UNSIGNED_INT, 0)
    PhiEngine.getInstance.checkGLError("after rendering a mesh")
  }
}