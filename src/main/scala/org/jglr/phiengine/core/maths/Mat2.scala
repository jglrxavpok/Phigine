package org.jglr.phiengine.core.maths

object Mat2 {
  val TMP = new Mat2()
}

class Mat2(_data: Array[Float] = null) extends Matrix(2, 2, _data){

  override def copy: Mat2 = super.copy.asInstanceOf[Mat2]
  override def *=(v: Float): Mat2 = super.*=(v).asInstanceOf[Mat2]
  override def *(v: Float): Mat2 = super.*(v).asInstanceOf[Mat2]

  def unary_~(): Mat2 = {
    invert()
  }

  def invert(): Mat2 = {
    this*(1f/determinant)
  }

  def determinant: Float = {
    get(0,0)*get(1,1) - get(0,1)*get(1,0)
  }
}
