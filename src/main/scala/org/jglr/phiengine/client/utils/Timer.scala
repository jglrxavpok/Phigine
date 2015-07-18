/*
 * The MIT License (MIT)
 *
 * Copyright ? 2014, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jglr.phiengine.client.utils

import org.lwjgl.glfw.GLFW.glfwGetTime

/**
 * The timer class is used for calculating delta time and also FPS and UPS
 * calculation.
 *
 * @author Heiko Brumme
 */
class Timer {
  /**
   * System time since last loop.
   */
  private var lastLoopTime: Double = .0
  /**
   * Used for FPS and UPS calculation.
   */
  private var timeCount: Float = 0
  /**
   * Frames per second.
   */
  private var fps: Int = 0
  /**
   * Counter for the FPS calculation.
   */
  private var fpsCount: Int = 0
  /**
   * Updates per second.
   */
  private var ups: Int = 0
  /**
   * Counter for the UPS calculation.
   */
  private var upsCount: Int = 0

  /**
   * Initializes the timer.
   */
  def init {
    lastLoopTime = getTime
  }

  /**
   * Returns the time elapsed since <code>glfwInit()</code> in seconds.
   *
   * @return System time in seconds
   */
  def getTime: Double = {
    return glfwGetTime
  }

  /**
   * Returns the time that have passed since the last loop.
   *
   * @return Delta time in seconds
   */
  def getDelta: Float = {
    val time: Double = getTime
    val delta: Float = (time - lastLoopTime).toFloat
    lastLoopTime = time
    timeCount += delta
    return delta
  }

  /**
   * Updates the FPS counter.
   */
  def updateFPS {
    fpsCount += 1
  }

  /**
   * Updates the UPS counter.
   */
  def updateUPS {
    upsCount += 1
  }

  /**
   * Updates FPS and UPS if a whole second has passed.
   */
  def update {
    if (timeCount > 1f) {
      fps = fpsCount
      fpsCount = 0
      ups = upsCount
      upsCount = 0
      timeCount -= 1f
    }
  }

  /**
   * Getter for the FPS.
   *
   * @return Frames per second
   */
  def getFPS: Int = {
    return if (fps > 0) fps else fpsCount
  }

  /**
   * Getter for the UPS.
   *
   * @return Updates per second
   */
  def getUPS: Int = {
    return if (ups > 0) ups else upsCount
  }

  /**
   * Getter for the last loop time.
   *
   * @return System time of the last loop
   */
  def getLastLoopTime: Double = {
    return lastLoopTime
  }
}