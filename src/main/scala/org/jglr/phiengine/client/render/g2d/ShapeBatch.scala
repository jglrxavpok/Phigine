package org.jglr.phiengine.client.render.g2d

import org.jglr.phiengine.client.render.{MeshUtils, Colors, Color, Shader}
import org.jglr.phiengine.core.maths.Constants._
import org.lwjgl.opengl.GL11._

/**
 * Represents how the shapes are drawn
 */
object ShapeMode extends Enumeration {
  type Type = Value
  val LINES, FILLED = Value
}

/**
 * Batch specialized in geometric shapes drawing
 * @param mode
 *             The mode to begin drawing with, defaults to FILLED
 * @param maxVertices
 *                    The number of maximum vertices used by the batch, defaults to 1024
 */
class ShapeBatch(val mode: ShapeMode.Type = ShapeMode.FILLED, val maxVertices: Int = 1024) extends Batch(maxVertices, if(mode == ShapeMode.LINES) maxVertices * 2 else maxVertices * 3,
  new Shader("assets/shaders/shapes.glsl")) {

  var shapeMode = mode

  /**
   * Begins drawing using given mode
   * @param mode
   *             The mode to draw with
   */
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

  /**
   * Checks if limit will be exceeded after `needed` vertices. If so, will flush the batch in order to create some place in memory
   * @param needed
   *               The number of vertices needed
   */
  def checkLimits(needed: Int = 0) = {
    if(offset >= verticesData.length-needed*MeshUtils.vertexSize)
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
    addVertex(x, y, z, 0, 0, bottomLeftCol)
    addVertex(x + width-1, y, z, 0, 0, bottomRightCol)
    addVertex(x + width-1, y + height-1, z, 0, 0, topRightCol)
    addVertex(x, y + height-1, z, 0, 0, topLeftCol)
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

  /**
   * Draws a circle.
   * @param x
   *          The x coordinate of center of the circle
   * @param y
   *          The y coordinate of center of the circle
   * @param z
   *          The z coordinate of center of the circle
   * @param radius
   *               The radius of the circle
   * @param color
   *              The color of the circle
   */
  def circle(x: Float, y: Float, z: Float, radius: Float, color: Color): Unit = {
    val segments: Int = Math.ceil(5.657 * radius).toInt
    circle(x, y, z, radius, segments, color)
  }

  /**
   * Draws a circle
   * @param x
   *          The x coordinate of center of the circle
   * @param y
   *          The y coordinate of center of the circle
   * @param z
   *          The z coordinate of center of the circle
   * @param radius
   *               The radius of the circle
   * @param segments
   *                 The number of segments to use to draw the circle
   * @param color
   *              The color of the circle
   */
  def circle(x: Float, y: Float, z: Float, radius: Float, segments: Int, color: Color): Unit = {
    arc(x, y, z, radius, 0f, TAU, segments, color, color)
  }

  /**
   * Draws an arc
   * @param x
   *          The x coordinate of center of the arc
   * @param y
   *          The y coordinate of center of the arc
   * @param z
   *          The z coordinate of center of the arc
   * @param radius
   *               The radius of the arc
   * @param startAngle
   *                   The angle of the beginning of the arc
   * @param angle
   *              The total angle of the arc
   * @param colorStart
   *              The color of the arc at `startAngle`
   * @param colorEnd
   *                 The color of the arc at `startAngle+angle`
   */
  def arc(x: Float, y: Float, z: Float, radius: Float, startAngle: Float, angle: Float, colorStart: Color, colorEnd: Color): Unit = {
    val segments: Int = Math.ceil(5.657 * radius).toInt
    arc(x,y,z,radius,startAngle,angle,segments, colorStart, colorEnd)
  }

  /**
   * Draws an arc
   * @param x
   *          The x coordinate of center of the arc
   * @param y
   *          The y coordinate of center of the arc
   * @param z
   *          The z coordinate of center of the arc
   * @param radius
   *               The radius of the arc
   * @param segments
   *                 The number of segments to use to draw the arc
   * @param startAngle
   *                   The angle of the beginning of the arc
   * @param angle
   *              The total angle of the arc
   * @param colorStart
   *              The color of the arc at `startAngle`
   * @param colorEnd
   *                 The color of the arc at `startAngle+angle`
   * @param colorCenter
   *                 The color at the center, used only in FILLED mode
   */
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
