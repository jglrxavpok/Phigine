package org.jglr.phiengine.core.maths

import org.joml.{Vector2f, Vector3f}

object VectorfExtensions {
  implicit class Vector2fExtensions(value: Vector2f) {
    def copy(): Vector2f = {
      new Vector2f(value)
    }

    def rotate(angle: Float): Vector2f = {
      val l: Float = value.length()
      val currentAngle: Float = Math.atan2(value.y, value.x).toFloat
      val newAngle: Float = currentAngle + angle
      val nx: Float = (Math.cos(newAngle) * l).toFloat
      val ny: Float = (Math.sin(newAngle) * l).toFloat
      value.set(nx, ny)
      value
    }
  }
  implicit class Vector3fExtensions(value: Vector3f) {
    def copy(): Vector3f = {
      new Vector3f(value)
    }
  }
}
