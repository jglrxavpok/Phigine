package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render.{Colors, Color, Shader}
import org.jglr.phiengine.core.maths.Constants._
import org.lwjgl.opengl.GL11._

object ShapeMode extends Enumeration {
  type Type = Value
  val LINES, FILLED = Value
}

class ShapeBatch(val mode: ShapeMode.Type = ShapeMode.FILLED, val maxVertices: Int = 4096) extends Batch(maxVertices, if(mode == ShapeMode.LINES) maxVertices * 2 else maxVertices * 3,
  new Shader("assets/shaders/shapes.glsl")) {

  var shapeMode = mode

  def begin(mode: ShapeMode.Type): Unit = {
    shapeMode = mode
    super.begin()
  }

  override def flush(): Unit = {
    if (offset == 0) {
      return
    }
    if (shader != null) shader.bind()
    mesh.updateVertices(verticesData, offset)
    mesh.updateIndices(indices, indexOffset)
    mesh.bind()
    mesh.render(if(shapeMode == ShapeMode.LINES) GL_LINES else GL_TRIANGLES)
    reset()
  }

  def checkLimits(needed: Int = 0) = {
    if(offset >= verticesData.length-needed*Batch.vertexSize)
      flush()
  }

  /**
   * Draws a rectangle.
   * @param x
   *         The bottom left corner x coordinate of the rectangle
   * @param y
   *         The bottom left corner y coordinate of the rectangle
   * @param z
   *         The zLevel of the rectangle
   * @param width
   *              The width of the rectangle
   * @param height
   *               The height of the rectangle
   * @param color
   *               <b>(Optional)</b> The color of the rectangle
   */
  def rectangle(x: Float, y: Float, z: Float, width: Float, height: Float, color: Color = Colors.niceWhite): Unit = {
    colrectangle(x,y,z,width,height,(color, color, color, color))
  }

  /**
   * Draws a rectangle.
   * @param x
   *         The bottom left corner x coordinate of the rectangle
   * @param y
   *         The bottom left corner y coordinate of the rectangle
   * @param z
   *         The zLevel of the rectangle
   * @param width
   *              The width of the rectangle
   * @param height
   *               The height of the rectangle
   * @param colors
   *               <b>(Optional)</b> Specify the colors of each corner of the rectangle in clockwise order starting from the bottom left corner
   */
  def colrectangle(x: Float, y: Float, z: Float, width: Float, height: Float, colors: (Color, Color, Color, Color) =
    (Colors.niceWhite, Colors.niceWhite, Colors.niceWhite, Colors.niceWhite)): Unit = {
    val bottomLeftCol = colors._1
    val topLeftCol = colors._2
    val topRightCol = colors._3
    val bottomRightCol = colors._4
    val offset = if(shapeMode == ShapeMode.LINES) 1 else 0
    addVertex(x, y-offset/*-offset to avoid an empty pixel in bottom left corner, dirty hack :c*/, z, 0, 0, bottomLeftCol)
    addVertex(x + width - offset, y, z, 0, 0, bottomRightCol)
    addVertex(x + width - offset, y + height - offset, z, 0, 0, topRightCol)
    addVertex(x, y + height - offset, z, 0, 0, topLeftCol)
    if(shapeMode == ShapeMode.FILLED) {
      addIndex(1)
      addIndex(0)
      addIndex(2)

      addIndex(2)
      addIndex(0)
      addIndex(3)
    } else {
      addIndex(0)
      addIndex(1)

      addIndex(1)
      addIndex(2)

      addIndex(2)
      addIndex(3)

      addIndex(3)
      addIndex(0)
    }

    addToCursor(4)
    checkLimits()
  }

  def circle(x: Float, y: Float, z: Float, radius: Float, color: Color): Unit = {
    val segments: Int = Math.ceil(5.657 * radius).toInt
    circle(x, y, z, radius, segments, color)
  }

  def circle(x: Float, y: Float, z: Float, radius: Float, segments: Int, color: Color): Unit = {
    arc(x, y, z, radius, 0f, TAU, segments, color, color)
  }

  def arc(x: Float, y: Float, z: Float, radius: Float, startAngle: Float, angle: Float, colorStart: Color, colorEnd: Color): Unit = {
    val segments: Int = Math.ceil(5.657 * radius).toInt
    arc(x,y,z,radius,startAngle,angle,segments, colorStart, colorEnd)
  }

  def arc(x: Float, y: Float, z: Float, radius: Float, startAngle: Float, angle: Float, segments: Int, colorStart: Color, colorEnd: Color, colorCenter: Color = null): Unit = {
    val centerCol = if(colorCenter == null) (colorStart + colorEnd) / 2 else colorCenter
    val delta: Float = angle / segments.toFloat
    var alpha: Float = startAngle
    var prevX = (Math.cos(startAngle) * radius + x).toFloat
    var prevY = (Math.sin(alpha) * radius + y).toFloat

    val neededVertices = if(shapeMode == ShapeMode.FILLED) 3 else 2
    for(i <- 0 to segments) {
      val x1: Float = (Math.cos(alpha) * radius + x).toFloat
      val y1: Float = (Math.sin(alpha) * radius + y).toFloat
      val progress = i/segments.toFloat
      checkLimits(neededVertices)
      addVertex(prevX, prevY, z, 0,0, colorStart.lerp(colorEnd, progress-delta))
      addVertex(x1, y1, z, 0,0, colorStart.lerp(colorEnd, progress))
      if(shapeMode == ShapeMode.FILLED) {
        addVertex(x, y, z, 0,0, centerCol)
        addIndex(1)
        addIndex(0)
        addIndex(2)
        addToCursor(3)
      } else {
        addIndex(0)
        addIndex(1)
        addToCursor(2)
      }
      prevX = x1
      prevY = y1
      alpha += delta
    }
  }

}
