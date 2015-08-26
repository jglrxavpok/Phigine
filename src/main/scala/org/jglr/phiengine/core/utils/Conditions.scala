package org.jglr.phiengine.core.utils

object Conditions {
  def checkRange(x: Float, min: Float, max: Float): Boolean = {
    x >= min && x < max
  }
}
