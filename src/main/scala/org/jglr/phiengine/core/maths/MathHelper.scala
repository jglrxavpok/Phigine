package org.jglr.phiengine.core.maths

object MathHelper {
  /**
   * From http://graphics.stanford.edu/~seander/bithacks.html
   */
  def upperPowerOf2(value: Int): Int = {
    var v = value
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
