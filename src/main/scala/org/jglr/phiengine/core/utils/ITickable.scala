package org.jglr.phiengine.core.utils

/**
 * Represents an object that updates itself every frame
 */
trait ITickable {
  /**
   * Ticks this object
   * @param delta
   *              The time in milliseconds between the last two frames
   */
  def tick(delta: Float)
}