package org.jglr.phiengine.core.maths

import org.joml.{Vector3f, Vector2f}

object MathHelper {

  val Pi = Math.PI
  val Tau = 2.0*Pi

  val Pif = Pi.toFloat
  val Tauf = Tau.toFloat
  
  val xAxis2f = new Vector2f(1,0)
  val xAxis3f = new Vector3f(1,0,0)
  val yAxis2f = new Vector2f(0,1)
  val yAxis3f = new Vector3f(0,1,0)
  val zAxis3f = new Vector3f(0,0,1)

  /**
   * From http://graphics.stanford.edu/~seander/bithacks.html
   */
  def upperPowerOf2(value: Int): Int = {
    var v = value
    if(v == 0) {
      1
    } else {
      v -= 1
      v |= v >> 1
      v |= v >> 2
      v |= v >> 4
      v |= v >> 8
      v |= v >> 16
      v |= v >> 32
      v += 1
      v
    }
  }

  private implicit def toArray(value: Seq[Double]): Array[Double] = {
    val arr = new Array[Double](value.length)
    value.copyToArray(arr)
    arr
  }

  def sin(angles: Double*): Double = {
    val result = new Array[Double](angles.length)
    info.yeppp.Math.Sin_V64f_V64f(angles, 0, result, 0, 1)
    result(0)
  }

  def tan(angles: Double*): Double = {
    val result = new Array[Double](angles.length)
    info.yeppp.Math.Tan_V64f_V64f(angles, 0, result, 0, 1)
    result(0)
  }

  def cos(angles: Double*): Double = {
    val result = new Array[Double](angles.length)
    info.yeppp.Math.Cos_V64f_V64f(angles, 0, result, 0, 1)
    result(0)
  }

}
