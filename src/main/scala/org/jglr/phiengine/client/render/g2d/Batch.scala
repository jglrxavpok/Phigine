package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render.{Colors, Color, Shader, Mesh}

object Batch {
  val vertexSize: Int = 3 + 2 + 4
}

abstract class Batch(verticesCount: Int, indicesCount: Int, defaultShader: Shader = null) {
  protected val verticesData = new Array[Float](verticesCount)
  protected val indices = new Array[Int](indicesCount)
  protected val mesh = new Mesh(verticesCount, indicesCount)
  protected var indexOffset: Int = 0
  protected var indexCursor: Int = 0
  protected var offset: Int = 0
  protected var drawing: Boolean = false
  protected var shader: Shader = null

  shader = defaultShader

  def setShader(s: Shader) {
    shader = s
  }

  def getShader: Shader = {
    shader
  }

  def end() {
    flush()
    if (shader != null) shader.unbind()
    drawing = false
  }

  def flush(): Unit

  def begin() {
    if (drawing) {
      throw new IllegalStateException("Cannot start drawing while already drawing")
    }
    reset()
    drawing = true
  }

  protected def reset() {
    indexCursor = 0
    offset = 0
    indexOffset = 0
  }

  def addIndex(index: Int) {
    addRawIndex(index + indexCursor)
  }

  def addRawIndex(index: Int) = {
    indices(indexOffset) = index
    indexOffset += 1
  }

  def addVertex(x: Float, y: Float, z: Float, u: Float, v: Float, color: Color = Colors.white): Unit = {
    addVertexData(x)
    addVertexData(y)
    addVertexData(z)
    addVertexData(u)
    addVertexData(v)
    addVertexData(color.r)
    addVertexData(color.g)
    addVertexData(color.b)
    addVertexData(color.a)
  }

  def addVertexData(value: Float): Unit = {
    verticesData(offset) = value
    offset += 1
  }

  def addToCursor(i: Int) {
    indexCursor += i
  }
}
