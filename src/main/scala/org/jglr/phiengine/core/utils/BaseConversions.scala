package org.jglr.phiengine.core.utils

object BaseConversions {
  implicit class ExtendedInt(value: Int) {
    def toHex: String = {
      Integer.toHexString(value).toUpperCase
    }
  }
}
