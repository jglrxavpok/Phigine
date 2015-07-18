package org.jglr.phiengine.core.maths

object Vec2 {
  /**
   * Vector used when needed to perform calculations. Reset it with your values each time you use it.
   */
  val TMP: Vec2 = new Vec2
}

class Vec2(var x: Float = 0, var y: Float = 0) {

  def apply(x: Float, y: Float): Vec2 = {
    set(x, y)
  }

  def set(x: Float, y: Float): Vec2 = {
    this.x = x
    this.y = y
    this
  }

  def +=(other: Vec2): Vec2 = {
    this +=(other.x, other.y)
  }

  def +=(x: Float, y: Float): Vec2 = {
    apply(this.x + x, this.y + y)
  }

  def -=(other: Vec2): Vec2 = {
    this -= (other.x, other.y)
  }

  def -=(x: Float, y: Float): Vec2 = {
    apply(this.x - x, this.y - y)
  }

  def *=(other: Vec2): Vec2 = {
    *=(other.x, other.y)
  }

  def *=(x: Float, y: Float): Vec2 = {
    apply(this.x * x, this.y * y)
  }

  def rotate(angle: Float): Vec2 = {
    val l: Float = length
    val currentAngle: Float = Math.atan2(y, x).toFloat
    val newAngle: Float = currentAngle + angle
    val nx: Float = (Math.cos(newAngle) * l).toFloat
    val ny: Float = (Math.sin(newAngle) * l).toFloat
    apply(nx, ny)
    this
  }

  def ndot(other: Vec2): Float = {
    ndot(other.x, other.y)
  }

  def ndot(x: Float, y: Float): Float = {
    val l: Float = length
    val nx: Float = x / l
    val ny: Float = y / l
    x * nx + y * ny
  }

  def isNormalized: Boolean = {
    lengthSquared == 1f
  }

  def length: Float = {
    Math.sqrt(lengthSquared).toFloat
  }

  def unary_~(): Float = length

  private def lengthSquared: Float = {
    x * x + y * y
  }

  def normalize: Vec2 = {
    if (isNull) return new Vec2
    val l: Float = length
    val nx: Float = x / l
    val ny: Float = y / l
    apply(nx, ny)
    this
  }

  def isNull: Boolean = {
    x == 0f && y == 0f
  }

  def unary_-(): Vec2 = {
    apply(-x, -y)
  }

  def getX: Float = {
    x
  }

  def getY: Float = {
    y
  }

  override def toString: String = {
    "vec2(" + x + "," + y + ")"
  }
}