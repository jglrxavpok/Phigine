package org.jglr.phiengine.core.maths

import org.joml.Vector3f

object Vector3fExtensions {
  implicit class Vector3fExtensions(value: Vector3f) {
    def copy(): Vector3f = {
      new Vector3f(value)
    }
  }
}
