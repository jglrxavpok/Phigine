package org.jglr.phiengine.core.utils

object StringUtils {
  def sum(array: Array[String], inBetween: String): String = {
    sum(array, inBetween, 0, array.length)
  }

  def sum(array: Array[String], inBetween: String, offset: Int, length: Int): String = {
    val builder: StringBuilder = new StringBuilder
    for(i <- 0 until length) {
      if (i != 0) builder.append(inBetween)
      builder.append(array(i))
    }
    builder.toString()
  }

  def createCorrectedFileName(name: String): String = {
    val buffer: StringBuilder = new StringBuilder
    for(i <- 0 until name.length) {
      var c: Char = name.charAt(i)
      if (c == ':' || c == '?' || c == '/' || c == '\\') c = '_'
      buffer.append(c)
    }
    buffer.toString()
  }
}