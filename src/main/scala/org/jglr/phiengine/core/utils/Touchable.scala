package org.jglr.phiengine.core.utils

/**
 * Used where you need to init an object way before its utilisation
 */
trait Touchable {
  def touch(): Unit = {}
}
