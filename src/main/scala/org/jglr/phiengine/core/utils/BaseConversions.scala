package org.jglr.phiengine.core.utils

import scala.reflect.ClassTag

object BaseConversions {
  implicit class ExtendedInt(value: Int) {
    def toHex: String = {
      Integer.toHexString(value).toUpperCase
    }
  }
}
