package org.jglr.phiengine.core.utils

/**
 * Represents an object that needs cleanup
 */
trait IDisposable {

  /**
   * Called when cleaning up the object
   */
  def dispose()
}