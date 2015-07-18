package org.jglr.phiengine.core.utils

object ArrayUtils {
  def getOr[A](array: Array[A], index: Int, default: A): A = {
    if(index >= array.length)
      default
    else if(index < 0)
      default
    else
      array(index)
  }
}
