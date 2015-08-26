package org.jglr.phiengine.core.maths

import java.nio._

class Mat4(_data: Array[Float] = null) extends Matrix(4, 4, _data) {

  def *(other: Mat4): Mat4 = {
    val res: Mat4 = new Mat4
    for(i <- 0 until 4) {
      for(j <- 0 until 4) {
          val firstRow: Float = get(i, 0) * other.get(0, j)
          val secondRow: Float = get(i, 1) * other.get(1, j)
          val thirdRow: Float = get(i, 2) * other.get(2, j)
          val fourthRow: Float = get(i, 3) * other.get(3, j)
          res.set(i, j, firstRow + secondRow + thirdRow + fourthRow)
      }
    }
    res
  }

  def *=(other: Mat4): Mat4 = {
    val res: Array[Float] = new Array[Float](16)
    for(i <- 0 until 4) {
      for(j <- 0 until 4) {
        val firstRow: Float = get(i, 0) * other.get(0, j)
        val secondRow: Float = get(i, 1) * other.get(1, j)
        val thirdRow: Float = get(i, 2) * other.get(2, j)
        val fourthRow: Float = get(i, 3) * other.get(3, j)
        res(j + i * 4) = firstRow + secondRow + thirdRow + fourthRow
      }
    }
    set(res)
    this
  }

  override def copy: Mat4 = new Mat4(data)
  override def *=(v: Float): Mat4 = super.*=(v).asInstanceOf[Mat4]
  override def *(v: Float): Mat4 = super.*(v).asInstanceOf[Mat4]
  override def transpose: Mat4 = super.transpose.asInstanceOf[Mat4]
  override def identity: Mat4 = super.identity.asInstanceOf[Mat4]

  def translation(x: Float, y: Float, z: Float): Mat4 = {
    identity
    set(0, 3, x)
    set(1, 3, y)
    set(2, 3, z)
    return this
  }

  def scale(x: Float, y: Float, z: Float): Mat4 = {
    identity
    set(0, 0, x)
    set(1, 1, y)
    set(2, 2, z)
    return this
  }

  def rotation(x: Float, y: Float, z: Float): Mat4 = {
    val rotx: Mat4 = new Mat4().identity
    val roty: Mat4 = new Mat4().identity
    val rotz: Mat4 = new Mat4().identity
    rotx.set(1, 1, Math.cos(x).toFloat)
    rotx.set(1, 2, -Math.sin(x).toFloat)
    rotx.set(2, 1, Math.sin(x).toFloat)
    rotx.set(2, 2, Math.cos(x).toFloat)

    roty.set(0, 0, Math.cos(y).toFloat)
    roty.set(0, 2, -Math.sin(y).toFloat)
    roty.set(2, 0, Math.sin(y).toFloat)
    roty.set(2, 2, Math.cos(y).toFloat)

    rotz.set(0, 0, Math.cos(z).toFloat)
    rotz.set(0, 1, -Math.sin(z).toFloat)
    rotz.set(1, 0, Math.sin(z).toFloat)
    rotz.set(1, 1, Math.cos(z).toFloat)
    return set(rotz.*(roty.*(rotx)))
  }

  def perspective(fov: Float, aspectRatio: Float, near: Float, far: Float): Mat4 = {
    all(0)
    val tanHlfFov: Float = Math.tan(fov / 2).toFloat
    val zRange: Float = near - far
    set(0, 0, 1.0f / (tanHlfFov * aspectRatio))
    set(1, 1, 1.0f / tanHlfFov)
    set(2, 2, (-near - far) / zRange)
    set(2, 3, 2 * far * near / zRange)
    set(3, 2, 1)
    return this
  }

  def orthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Mat4 = {
    all(0)
    val width: Float = right - left
    val height: Float = top - bottom
    val depth: Float = far - near
    set(0, 0, 2f / width)
    set(0, 3, -(right + left) / width)
    set(1, 1, 2f / height)
    set(1, 3, -(top + bottom) / height)
    set(2, 2, -2f / depth)
    set(2, 3, -(far + near) / depth)
    set(3, 3, 1f)
    return this
  }

  def set(other: Mat4): Mat4 = {
    for(i <- data.indices) {
      data(i) = other.data(i)
    }
    return this
  }

  override def set(x: Int, y: Int, value: Float): Mat4 = super.set(x,y,value).asInstanceOf[Mat4]

  def unary_~(): Mat4 = {
    invert()
  }

  def invert(): Mat4 = {
    val deter = determinant
    if(deter != 0) {
      val transposed = copy transpose
      val subdeters = new Mat4()
      for(i <- 0 until 4) {
        for(j <- 0 until 4) {
          subdeters(i, j) = transposed.cofactor(i, j)
        }
      }
      subdeters*(1f/deter)
    } else {
      null
    }
  }

  def determinant: Float = {
    val d00 = cofactor(0,0)
    val d10 = cofactor(1,0)
    val d20 = cofactor(2,0)
    val d30 = cofactor(3,0)
    d00 * get(0, 0) + d10 * get(1, 0) + d20 * get(2, 0) + d30 * get(3, 0)
  }

  def minor(x: Int, y: Int): Float = {
    val submat = Mat3.TMP
    submat.all(0)
    var _x = 0
    var _y = 0
    for(j <- 0 until columns) {
      for(i <- 0 until rows) {
        if(i != x && j != y) {
          submat(_x, _y) = get(i, j)
          _x += 1
        }
      }
      if(j != y)
        _y+=1
      _x = 0
    }
    submat.determinant
  }

  def cofactor(x: Int, y: Int): Float = {
    val minorval = minor(x,y)
    if((x+y % 2) == 0)
      -minorval
    else
      minorval
  }

  def transform(r: Vec3): Vec3 = {
    val x: Float = get(0, 0) * r.x + get(0, 1) * r.y + get(0, 2) * r.z + get(0, 3)
    val y: Float = get(1, 0) * r.x + get(1, 1) * r.y + get(1, 2) * r.z + get(1, 3)
    val z: Float = get(2, 0) * r.x + get(2, 1) * r.y + get(2, 2) * r.z + get(2, 3)
    r.x = x
    r.y = y
    r.z = z
    r
  }
}