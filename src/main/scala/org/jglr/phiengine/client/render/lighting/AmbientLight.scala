package org.jglr.phiengine.client.render.lighting

import org.jglr.phiengine.client.render.Shader
import org.joml.{Vector3f, Vector4f}

class AmbientLight(color: Vector3f) extends Light(color) {

  attenuationCoefficients.set(-1,-1,-1)

  override def writeToShader(shader: Shader, prefix: String): Unit = {
    super.writeToShader(shader, prefix)
  }
}
