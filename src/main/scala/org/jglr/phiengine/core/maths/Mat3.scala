package org.jglr.phiengine.core.maths

object Mat3 {
  val TMP = new Mat3()
}

class Mat3(_data: Array[Float] = null) extends Matrix(3, 3, _data) {

  override def copy: Mat3 = super.copy.asInstanceOf[Mat3]
  override def *=(v: Float): Mat3 = super.*=(v).asInstanceOf[Mat3]
  override def *(v: Float): Mat3 = super.*(v).asInstanceOf[Mat3]
  override def transpose: Mat3 = super.transpose.asInstanceOf[Mat3]

  def unary_~(): Mat3 = {
    invert()
  }

  def invert(): Mat3 = {
    val deter = determinant
    if(deter != 0) {
      val transposed = copy transpose
      val subdeters = new Mat3()
      for(i <- 0 until 3) {
        for(j <- 0 until 3) {
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
    d00 * get(0, 0) + d10 * get(1, 0) + d20 * get(2, 0)
  }

  def minor(x: Int, y: Int): Float = {
    val submat = Mat2.TMP
    submat.all(0)
    var _x = 0
    var _y = 0
    for(j <- 0 until columns) {
      for(i <- 0 until rows) {
        if(x != i && y != j) {
          submat(_x, _y) = get(i, j)
          _x += 1
        }
      }
      if(y != j)
        _y+=1
      _x = 0
    }
    submat.determinant
  }

  def cofactor(x: Int, y: Int): Float = {
    val minorval = minor(x,y)
    if(x+y % 2 == 0)
      minorval
    else
      -minorval
  }
}
