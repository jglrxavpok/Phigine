package org.jglr.phiengine.core.maths

object Vec3 {
  /**
   * Vector used when needed to perform calculations. Reset it with your values each time you use it.
   */
  val TMP: Vec3 = new Vec3
}

class Vec3(var x: Float = 0, var y: Float = 0, var z: Float = 0) {
  def *(fl: Float): Vec3 = {
    this * (fl, fl, fl)
  }

  def /(fl: Float): Vec3 = {
    this / (fl, fl, fl)
  }

  def /(x: Float, y: Float, z: Float): Vec3 = {
    this * (1f/x, 1f/y, 1f/z)
  }

  def xy = new Vec2(x,y)

  def +=(x: Float, y: Float, z: Float): Vec3 = {
    set(this.x + x, this.y + y, this.z + z)
  }

  def +=(other: Vec3): Vec3 = {
    this += (other.x, other.y, other.z)
  }

  def apply(x: Float, y: Float, z: Float): Vec3 = {
    set(x,y,z)
  }

  def apply(tuple: (Float, Float, Float)): Vec3 = {
    set(tuple._1,tuple._2,tuple._3)
  }

  def apply(other: Vec3): Vec3 = {
    set(other.x, other.y, other.z)
  }

  def set(x: Float, y: Float, z: Float): Vec3 = {
    this.x = x
    this.y = y
    this.z = z
    return this
  }

  def +(other: Vec3): Vec3 = {
    this + (other.x, other.y, other.z)
  }

  def +(x: Float, y: Float, z: Float): Vec3 = {
    new Vec3(this.x + x, this.y + y, this.z + z)
  }

  def -(other: Vec3): Vec3 = {
    this -(other.x, other.y, other.z)
  }

  def -(x: Float, y: Float, z: Float): Vec3 = {
    new Vec3(this.x - x, this.y - y, this.z - z)
  }

  def -=(x: Float, y: Float, z: Float): Vec3 = {
    set(this.x - x, this.y - y, this.z - z)
  }

  def *(other: Vec3): Vec3 = {
    this * (other.x, other.y, other.z)
  }

  def *(x: Float, y: Float, z: Float): Vec3 = {
    new Vec3(this.x * x, this.y * y, this.z * z)
  }

  def ndot(other: Vec3): Float = {
    ndot(other.x, other.y, other.z)
  }

  def ndot(x: Float, y: Float, z: Float): Float = {
    val l: Float = length
    val nx: Float = x / l
    val ny: Float = y / l
    val nz: Float = z / l
    return x * nx + y * ny + z * nz
  }

  def isNormalized: Boolean = {
    return lengthSquared == 1f
  }

  def unary_~ = length

  def length: Float = {
    return Math.sqrt(lengthSquared).toFloat
  }

  private def lengthSquared: Float = {
    return x * x + y * y + z * z
  }

  def normalize: Vec3 = {
    if (isNull) return new Vec3
    val l: Float = length
    val nx: Float = x / l
    val ny: Float = y / l
    val nz: Float = z / l
    set(nx, ny, nz)
    return this
  }

  def isNull: Boolean = {
    return x == 0f && y == 0f && z == 0f
  }

  def unary_- = new Vec3(-x, -y, -z)

  def getX: Float = {
    return x
  }

  def getY: Float = {
    return y
  }

  def getZ: Float = {
    return z
  }

  override def toString: String = {
    return "vec3(" + x + "," + y + "," + z + ")"
  }

  implicit def toTuple(): (Float, Float, Float) = {
    (x,y,z)
  }
}