package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render.{Colors, Color, Shader, Mesh}

object Batch {
  /**
   * The size of a single vertex:<br/>
   * - 3 floats for position (x, y, z)<br/>
   * - 2 floats for texture coordinates (u, v)<br/>
   * - 4 floats for vertex color (r, g, b, a)
   */
  val vertexSize: Int = 3 + 2 + 4
}

/**
 * Class used for rendering inside with a mesh.<br/>
 * '''''Each creation of a Batch object allocates a mesh of ''at least'' {@code (vertexSize * verticesCount << 2)+(vertexSize * indicesCount << 2)} bytes inside the GPU memory!'''''
 * @param verticesCount
 *                      The maximum number of vertices
 * @param indicesCount
 *                     The maximum number of indices
 * @param defaultShader
 *                      The default shader to use, if any
 */
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

  def isDrawing = drawing

  def setShader(s: Shader) {
    shader = s
  }

  def getShader: Shader = {
    shader
  }

  /**
   * Finishes drawing, flushing and unbinding the shader if there is one
   */
  def end() {
    flush()
    if (shader != null) shader.unbind()
    drawing = false
  }

  /**
   * Renders the content of the mesh
   */
  def flush(): Unit

  /**
   * Begins drawing inside the mesh
   */
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

  /**
   * Adds an index relative to the current cursor
   * @param index
   *              The index relative to the cursor
   */
  def addIndex(index: Int) {
    addRawIndex(index + indexCursor)
  }

  /**
   * Adds an index to the mesh
   * @param index
   *              The index to add
   */
  def addRawIndex(index: Int) = {
    indices(indexOffset) = index
    indexOffset += 1
  }

  /**
   * Specifies a vertex inside the mesh
   * @param x
   *          The x coordinate
   * @param y
   *          The y coordinate
   * @param z
   *          The z coordinate
   * @param u
   *          The u coordinate (texture coordinate)
   * @param v
   *          The v coordinate (texture coordinate)
   * @param color
   *              The color of the vertex
   */
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

  /**
   * Adds a single value to the vertex data, users are discouraged to use this method.
   * @param value
   *              The value to add the the vertex data
   */
  def addVertexData(value: Float): Unit = {
    verticesData(offset) = value
    offset += 1
  }

  /**
   * Increments the index cursor by i
   * @param i
   *          The incrementation value
   */
  def addToCursor(i: Int) {
    indexCursor += i
  }
}
